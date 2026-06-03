package com.erp.vpac.controller;

import com.erp.vpac.dto.ApiResponse;
import com.erp.vpac.dto.request.CreateRoleRequest;
import com.erp.vpac.dto.response.RoleResponse;
import com.erp.vpac.service.RolePermissionService;
import com.erp.vpac.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final RolePermissionService rolePermissionService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<RoleResponse> createRole(
            @RequestBody CreateRoleRequest request
    ) {
        var result = roleService.createRole(request);
        return ApiResponse.<RoleResponse>builder().result(result).build();
    }
    @PostMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> assignPermissionToRole(
            @PathVariable Long roleId,
            @PathVariable Long permissionId
    ) {
        rolePermissionService.assignPermissionToRole(roleId, permissionId);
        return ApiResponse.<String>builder().message("Permission assigned to role successfully").build();

    }
}
