package com.kbsl.kbslserver.user.domain.repository;

import com.kbsl.kbslserver.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String username);
    Optional<User> findByNickname(String nickname);
}
