package com.erp.vpac.security;

import com.erp.vpac.entity.User;
import com.erp.vpac.entity.UserSession;
import com.erp.vpac.exception.AppException;
import com.erp.vpac.exception.ErrorCode;
import com.erp.vpac.repository.UserSessionRepository;
import com.erp.vpac.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionTokenAuthenticationFilter extends OncePerRequestFilter {
    private final UserSessionRepository userSessionRepository;
    private final AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        String token = extractToken(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        log.warn("token");
        log.warn(token);
        UserSession userSession = userSessionRepository.findValidSession(token, LocalDateTime.now())
                .orElseThrow(() -> new AppException(ErrorCode.TOKEN_INVALID));
        User user = userSession.getUser();
        log.warn("user");
        log.warn(user.toString());
        List<GrantedAuthority> authorities =
                authService.getAuthorities(user.getId());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        authorities
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);


        ;

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("SESSION_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}

