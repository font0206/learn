package com.erp.vpac.repository;

import com.erp.vpac.entity.RolePermission;
import com.erp.vpac.entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
    List<RolePermission> findByRoleId(Long roleId);
}
