package com.erp.vpac.dto.request;

public record CreateRoleRequest(
        String code,
        String name,
        String description
) {
}
