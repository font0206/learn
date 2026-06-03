package com.erp.vpac.controller;

import com.erp.vpac.dto.ApiResponse;
import com.erp.vpac.dto.request.CreatePermissionRequest;
import com.erp.vpac.dto.response.PermissionResponse;
import com.erp.vpac.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PermissionResponse> createPermission(
            @RequestBody CreatePermissionRequest request
    ) {
        return ApiResponse.<PermissionResponse>
                        builder()
                .result(permissionService.createPermission(request))
                .build();

    }
}
