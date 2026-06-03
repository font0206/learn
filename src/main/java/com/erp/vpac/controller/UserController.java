package com.erp.vpac.controller;

import com.erp.vpac.dto.ApiResponse;
import com.erp.vpac.dto.response.UserResponse;
import com.erp.vpac.entity.User;
import com.erp.vpac.service.UserRoleService;
import com.erp.vpac.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;
    UserRoleService userRoleService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMe(@AuthenticationPrincipal User user) {
        var result = UserResponse.builder().id(user.getId()).username(user.getUsername()).enabled(user.getEnabled()).build();
        return ApiResponse.<UserResponse>builder().result(result).build();

    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<UserResponse>> getUsers() {
        var result = userService.getUsers();
        return ApiResponse.<List<UserResponse>>builder().result(result).build();

    }

    @PostMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> assignRoleToUser(
            @PathVariable Long userId,
            @PathVariable Long roleId
    ) {
        userRoleService.assignRoleToUser(userId, roleId);
        return ApiResponse.<String>builder().message("Role assigned to user successfully").build();
    }
}
