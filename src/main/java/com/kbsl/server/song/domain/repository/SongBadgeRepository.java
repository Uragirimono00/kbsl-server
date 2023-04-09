package com.kbsl.server.song.domain.repository;

import com.kbsl.server.song.domain.model.SongBadge;
import com.kbsl.server.song.enums.SongBadgeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface SongBadgeRepository extends JpaRepository<SongBadge, Long> {
    Optional<SongBadge> findBySongBadgeType(SongBadgeType songBadgeType);
}
