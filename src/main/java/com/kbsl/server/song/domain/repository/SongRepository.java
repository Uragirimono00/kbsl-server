package com.kbsl.server.song.domain.repository;

import com.kbsl.server.song.domain.model.Song;
import com.kbsl.server.song.dto.response.SongApiResponseDto;
import com.kbsl.server.song.enums.SongDifficultyType;
import com.kbsl.server.song.enums.SongModeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {
    Optional<Song> findBySeq(Long songSeq);

    Song findBySongModeTypeAndSongHashAndSongDifficulty(SongModeType songModeType, String songHash, SongDifficultyType songDifficulty);

    List<SongApiResponseDto> findBySongId(String id);

    List<SongApiResponseDto> findBySongHash(String hash);
}
