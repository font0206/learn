package com.erp.vpac.repository;

import com.erp.vpac.entity.UserRole;
import com.erp.vpac.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    @Query("""
        select ur
        from UserRole ur
        join fetch ur.role
        where ur.user.id = :userId
    """)
    List<UserRole> findByUserIdWithRole(@Param("userId") Long userId);
    boolean existsByUserIdAndRoleId(Long userId, Long roleId);
}
