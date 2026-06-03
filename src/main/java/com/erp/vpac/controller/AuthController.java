package com.erp.vpac.controller;

import com.erp.vpac.dto.ApiResponse;
import com.erp.vpac.dto.request.LoginRequest;
import com.erp.vpac.dto.response.LoginResponse;
import com.erp.vpac.dto.request.RegisterRequest;
import com.erp.vpac.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

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
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse result = authService.login(request);
        ResponseCookie cookie = ResponseCookie.from("SESSION_TOKEN", result.token())
                .httpOnly(true)
                .secure(false) // local dùng false, production HTTPS thì true
                .path("/")
                .maxAge(Duration.ofHours(2))
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ApiResponse.<LoginResponse>builder().result(result).build();
    }

    //    @PostMapping("/logout")
//    public ApiResponse<String> logout(
//            @RequestHeader("Authorization") String authorization
//    ) {
//        String token = authorization.substring(7);
//        authService.logout(token);
//        return ApiResponse.<String>builder().message("Logout successfully").build();
//    }
    @PostMapping("/logout")
    public ApiResponse<String> logout(
            @CookieValue(name = "SESSION_TOKEN", required = false) String token,
            HttpServletResponse response
    ) {
        if (token != null) {
            authService.logout(token);
        }
        ResponseCookie cookie = ResponseCookie.from("SESSION_TOKEN", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ApiResponse.<String>builder().message("Logout successfully").build();
    }

}
