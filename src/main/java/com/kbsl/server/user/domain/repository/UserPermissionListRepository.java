package com.kbsl.server.user.domain.repository;

import com.kbsl.server.user.domain.model.User;
import com.kbsl.server.user.domain.model.UserPermission;
import com.kbsl.server.user.domain.model.UserPermissionList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPermissionListRepository extends JpaRepository<UserPermissionList, Long> {
    Optional<UserPermissionList> findByUserAndPermission(User userEntity, UserPermission userPermissionEntity);

    boolean existsByUserAndPermission(User userEntity, UserPermission userPermissionEntity);
}
