package com.erp.vpac.service;

import com.erp.vpac.entity.Permission;
import com.erp.vpac.entity.Role;
import com.erp.vpac.entity.RolePermission;
import com.erp.vpac.entity.RolePermissionId;
import com.erp.vpac.repository.PermissionRepository;
import com.erp.vpac.repository.RolePermissionRepository;
import com.erp.vpac.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RolePermissionService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RolePermissionRepository rolePermissionRepository;
    @Transactional
    public void assignPermissionToRole(Long roleId, Long permissionId) {
        if (rolePermissionRepository.existsByRoleIdAndPermissionId(roleId, permissionId)) {
            throw new RuntimeException("Permission already assigned to role");
        }

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        RolePermission rolePermission = RolePermission.builder()
                .id(new RolePermissionId(roleId, permissionId))
                .role(role)
                .permission(permission)
                .assignedAt(LocalDateTime.now())
                .assignedBy("SYSTEM")
                .build();

        rolePermissionRepository.save(rolePermission);
    }

}
