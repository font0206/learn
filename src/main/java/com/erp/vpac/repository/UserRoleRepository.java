package com.erp.vpac.repository;

import com.erp.vpac.entity.UserRole;
import com.erp.vpac.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

}
