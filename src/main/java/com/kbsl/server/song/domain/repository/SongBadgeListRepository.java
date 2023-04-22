package com.kbsl.server.song.domain.repository;

import com.kbsl.server.song.domain.model.SongBadge;
import com.kbsl.server.song.domain.model.SongBadgeList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SongBadgeListRepository extends JpaRepository<SongBadgeList, Long> {
    List<SongBadgeList> findAllBySongSeq(Long seq);
}
