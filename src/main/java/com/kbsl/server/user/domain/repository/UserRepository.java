package com.kbsl.server.user.domain.repository;

import com.kbsl.server.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByUsername(String email);
    Optional<User> findByUsername(String email);
    Optional<User> findBySeq(Long userSeq);
    Boolean existsBySteamId(String steamId);
    List<User> findBySteamIdIsNotNull();
}
