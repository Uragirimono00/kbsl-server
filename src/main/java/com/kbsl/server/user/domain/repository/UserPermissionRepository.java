package com.kbsl.server.user.domain.repository;

import com.kbsl.server.user.domain.model.UserPermission;
import com.kbsl.server.user.enums.UserPermissionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {
    Optional<UserPermission> findByUserPermissionType(UserPermissionType userPermissionType);
}
