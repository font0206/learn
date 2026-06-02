package com.erp.vpac.service;

import com.erp.vpac.dto.LoginRequest;
import com.erp.vpac.dto.LoginResponse;
import com.erp.vpac.dto.RegisterRequest;
import com.erp.vpac.entity.*;
import com.erp.vpac.repository.RoleRepository;
import com.erp.vpac.repository.UserRepository;
import com.erp.vpac.repository.UserRoleRepository;
import com.erp.vpac.repository.UserSessionRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserRoleRepository userRoleRepository;
    UserSessionRepository userSessionRepository;
    PasswordEncoder passwordEncoder;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEnabled(true);

        User savedUser = userRepository.save(user);

        Role defaultRole = roleRepository.findByCode("USER")
                .orElseThrow(() -> new RuntimeException("Default role USER not found"));

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
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new RuntimeException("Account is disabled");
        }

        boolean passwordMatches = passwordEncoder.matches(
                request.password(),
                user.getPassword()
        );

        if (!passwordMatches) {
            throw new RuntimeException("Invalid username or password");
        }

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

}
