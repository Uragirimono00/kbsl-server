package com.kbsl.server.song.domain.repository;

import com.kbsl.server.song.domain.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {
    Optional<Song> findBySeq(Long songSeq);
}
