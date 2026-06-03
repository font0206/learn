package com.erp.vpac.service;

import com.erp.vpac.dto.request.CreatePermissionRequest;
import com.erp.vpac.dto.response.PermissionResponse;
import com.erp.vpac.entity.Permission;
import com.erp.vpac.exception.AppException;
import com.erp.vpac.exception.ErrorCode;
import com.erp.vpac.repository.PermissionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;

    @Transactional
    public PermissionResponse createPermission(CreatePermissionRequest request) {
        String code = request.code().trim().toUpperCase();

        if (permissionRepository.existsByCode(code)) {
            throw new AppException(ErrorCode.PERMISSION_EXISTED);
        }

        Permission permission = Permission.builder()
                .code(code)
                .name(request.name())
                .description(request.description())
                .build();

        Permission savedPermission = permissionRepository.save(permission);

        return new PermissionResponse(
                savedPermission.getId(),
                savedPermission.getCode(),
                savedPermission.getName(),
                savedPermission.getDescription()
        );
    }
}
