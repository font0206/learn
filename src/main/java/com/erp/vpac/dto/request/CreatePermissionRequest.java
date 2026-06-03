package com.erp.vpac.dto.request;

public record CreatePermissionRequest(
        String code,
        String name,
        String description
) {
}
