package com.erp.vpac.dto.response;

public record PermissionResponse(
        Long id,
        String code,
        String name,
        String description
) {
}
