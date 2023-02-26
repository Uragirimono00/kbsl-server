package com.kbsl.server.user.domain.repository;

import com.kbsl.server.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    Optional<User> findBySeq(Long userSeq);
    Boolean existsBybeatleaderId(String beatleaderId);
}
