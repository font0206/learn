package com.erp.vpac.repository;

import com.erp.vpac.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    @Query("""
                select distinct u
                from User u
                left join fetch u.userRoles ur
                left join fetch ur.role
            """)
    List<User> findAllWithRoles();

}
