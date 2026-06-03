package com.erp.vpac.repository;

import com.erp.vpac.entity.RolePermission;
import com.erp.vpac.entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
    List<RolePermission> findByRoleId(Long roleId);
    @Query("""
        select rp
        from RolePermission rp
        join fetch rp.permission
        where rp.role.id in :roleIds
    """)
    List<RolePermission> findByRoleIdsWithPermission(@Param("roleIds") List<Long> roleIds);
    boolean existsByRoleIdAndPermissionId(Long roleId, Long permissionId);
}
