package com.erp.vpac.service;

import com.erp.vpac.dto.request.LoginRequest;
import com.erp.vpac.dto.response.LoginResponse;
import com.erp.vpac.dto.request.RegisterRequest;
import com.erp.vpac.entity.*;
import com.erp.vpac.exception.AppException;
import com.erp.vpac.exception.ErrorCode;
import com.erp.vpac.repository.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserRoleRepository userRoleRepository;
    UserSessionRepository userSessionRepository;
    RolePermissionRepository rolePermissionRepository;

    PasswordEncoder passwordEncoder;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEnabled(true);

        User savedUser = userRepository.save(user);

        Role defaultRole = roleRepository.findByCode("USER")
                .orElseThrow(() ->  new AppException(ErrorCode.ROLE_NOT_EXISTED));

        UserRole userRole = new UserRole();
        LocalDateTime assignTime = LocalDateTime.now();
        String assignedBy = user.getUsername();
        userRole.setId(new UserRoleId(savedUser.getId(), defaultRole.getId()));
        userRole.setUser(savedUser);
        userRole.setRole(defaultRole);
        userRole.setAssignedAt(assignTime);
        userRole.setAssignedBy(assignedBy);

        userRoleRepository.save(userRole);

    }
    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        boolean passwordMatches = passwordEncoder.matches(
                request.password(),
                user.getPassword()
        );

        if (!passwordMatches) {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }
        userSessionRepository.revokeAllByUserId(user.getId());
        String token = generateToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(2);

        UserSession session = UserSession.builder()
                .user(user)
                .token(token)
                .expiresAt(expiresAt)
                .revoked(false)
                .build();

        userSessionRepository.save(session);

        return new LoginResponse(token, expiresAt);
    }

    private String generateToken() {
        byte[] randomBytes = new byte[64];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }
    @Transactional
    public void logout(String token) {
        UserSession session = userSessionRepository
                .findByTokenAndRevokedFalse(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        session.setRevoked(true);
    }
    public List<GrantedAuthority> getAuthorities(Long userId) {
        List<UserRole> userRoles = userRoleRepository.findByUserIdWithRole(userId);

        List<GrantedAuthority> authorities = new ArrayList<>();

        List<Long> roleIds = new ArrayList<>();
        for (UserRole userRole : userRoles) {
            Role role = userRole.getRole();

            authorities.add(
                    new SimpleGrantedAuthority("ROLE_" + role.getCode())
            );

            roleIds.add(role.getId());
        }

        if (!roleIds.isEmpty()) {
            List<RolePermission> rolePermissions =
                    rolePermissionRepository.findByRoleIdsWithPermission(roleIds);

            for (RolePermission rolePermission : rolePermissions) {
                Permission permission = rolePermission.getPermission();

                authorities.add(
                        new SimpleGrantedAuthority(permission.getCode())
                );
            }
        }

        return authorities;
    }

}
