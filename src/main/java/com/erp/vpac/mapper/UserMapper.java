package com.erp.vpac.mapper;

import com.erp.vpac.dto.response.RoleResponse;
import com.erp.vpac.dto.response.UserResponse;
import com.erp.vpac.entity.User;
import com.erp.vpac.entity.UserRole;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {
    public UserResponse toUserResponse(User user) {
        List<RoleResponse> roles = user.getUserRoles()
                .stream()
                .map(UserRole::getRole)
                .map(role -> new RoleResponse(
                        role.getId(),
                        role.getCode(),
                        role.getName(),
                        role.getDescription()
                ))
                .toList();

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEnabled(),
                roles
        );
    }
}
