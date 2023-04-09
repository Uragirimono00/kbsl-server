package com.kbsl.server.song.domain.repository;

import com.kbsl.server.song.domain.model.SongBadgeList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongBadgeListRepository extends JpaRepository<SongBadgeList, Long> {
}
