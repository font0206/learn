package com.erp.vpac.service;

import com.erp.vpac.dto.request.CreateRoleRequest;
import com.erp.vpac.dto.response.RoleResponse;
import com.erp.vpac.entity.Role;
import com.erp.vpac.exception.AppException;
import com.erp.vpac.exception.ErrorCode;
import com.erp.vpac.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    @Transactional
    public RoleResponse createRole(CreateRoleRequest request) {
        String code = request.code().trim().toUpperCase();

        if (roleRepository.existsByCode(code)) {
            throw new AppException(ErrorCode.ROLE_EXISTED);
        }

        Role role = Role.builder()
                .code(code)
                .name(request.name())
                .description(request.description())
                .build();

        Role savedRole = roleRepository.save(role);

        return new RoleResponse(
                savedRole.getId(),
                savedRole.getCode(),
                savedRole.getName(),
                savedRole.getDescription()
        );
    }
}
