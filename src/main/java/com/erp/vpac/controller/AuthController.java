package com.erp.vpac.controller;

import com.erp.vpac.dto.ApiResponse;
import com.erp.vpac.dto.request.LoginRequest;
import com.erp.vpac.dto.response.LoginResponse;
import com.erp.vpac.dto.request.RegisterRequest;
import com.erp.vpac.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthController {
    AuthService authService;

    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ApiResponse.<String>builder().message("Register successfully").build();
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        var result = authService.login(request);
        return ApiResponse.<LoginResponse>builder().result(result).build();
    }
    @PostMapping("/logout")
    public ApiResponse<String> logout(
            @RequestHeader("Authorization") String authorization
    ) {
        String token = authorization.substring(7);
        authService.logout(token);
        return ApiResponse.<String>builder().message("Logout successfully").build();
    }

}
