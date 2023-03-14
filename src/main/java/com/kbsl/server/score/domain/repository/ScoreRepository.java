package com.kbsl.server.score.domain.repository;

import com.kbsl.server.score.domain.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    Optional<Score> findBySeq(Long userSeq);

    Optional<Score> findByScoreSeq(Long id);
}
