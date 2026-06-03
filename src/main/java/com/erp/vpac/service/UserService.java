package com.erp.vpac.service;

import com.erp.vpac.dto.response.UserResponse;
import com.erp.vpac.entity.User;
import com.erp.vpac.entity.UserSession;
import com.erp.vpac.mapper.UserMapper;
import com.erp.vpac.repository.UserRepository;
import com.erp.vpac.repository.UserSessionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    UserSessionRepository userSessionRepository;
    UserMapper userMapper;

    public User getUserByToken(String token) {
        UserSession userSession = userSessionRepository.findValidSession(token, LocalDateTime.now()
        ).orElseThrow(() -> new RuntimeException("Default role USER not found"));
        log.info("check", userSession);
        return userSession.getUser();
    }

    public List<UserResponse> getUsers() {
        return userRepository.findAllWithRoles()
                .stream()
                .map((user) -> userMapper.toUserResponse(user))
                .toList();
    }
}
