package com.erp.vpac.repository;
import com.erp.vpac.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    @Query("""
    select us
    from UserSession us
    join fetch us.user
    where us.token = :token
      and us.revoked = false
      and us.expiresAt > :now
""")
    Optional<UserSession> findValidSession(
            @Param("token") String token,
            @Param("now") LocalDateTime now
    );

    @Modifying
    @Query("""
    update UserSession us
    set us.revoked = true
    where us.user.id = :userId
      and us.revoked = false
""")
    void revokeAllByUserId(Long userId);
    Optional<UserSession> findByTokenAndRevokedFalseAndExpiresAtAfter(
            String token,
            LocalDateTime now
    );
    Optional<UserSession> findByTokenAndRevokedFalse(String token);
}
