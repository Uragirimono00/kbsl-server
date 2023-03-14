package com.kbsl.server.score.domain.repository;

import com.kbsl.server.score.domain.model.Score;
import com.kbsl.server.score.domain.repository.custom.ScoreRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score, Long>, ScoreRepositoryCustom {
    Optional<Score> findBySeq(Long userSeq);

    Optional<Score> findByScoreSeq(Long id);

    boolean existsByScoreSeq(long scoreSeq);
}
