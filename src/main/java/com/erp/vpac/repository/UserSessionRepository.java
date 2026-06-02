package com.erp.vpac.repository;
import com.erp.vpac.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByTokenAndRevokedFalseAndExpiresAtAfter(
            String token,
            LocalDateTime now
    );
}
