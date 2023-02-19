package com.kbsl.server.auth.domain.repository;

import com.kbsl.server.auth.domain.model.Role;
import com.kbsl.server.auth.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRole(ERole role);
}
