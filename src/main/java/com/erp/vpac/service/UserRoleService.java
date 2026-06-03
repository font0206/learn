package com.erp.vpac.service;

import com.erp.vpac.entity.Role;
import com.erp.vpac.entity.User;
import com.erp.vpac.entity.UserRole;
import com.erp.vpac.entity.UserRoleId;
import com.erp.vpac.repository.RoleRepository;
import com.erp.vpac.repository.UserRepository;
import com.erp.vpac.repository.UserRoleRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class UserRoleService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserRoleRepository userRoleRepository;
    @Transactional
    public void assignRoleToUser(Long userId, Long roleId) {
        if (userRoleRepository.existsByUserIdAndRoleId(userId, roleId)) {
            throw new RuntimeException("Role already assigned to user");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        UserRole userRole = UserRole.builder()
                .id(new UserRoleId(userId, roleId))
                .user(user)
                .role(role)
                .assignedAt(LocalDateTime.now())
                .assignedBy("SYSTEM")
                .build();

        userRoleRepository.save(userRole);
    }
}
