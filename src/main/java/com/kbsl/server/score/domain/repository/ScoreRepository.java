package com.kbsl.server.score.domain.repository;

import com.kbsl.server.score.domain.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreRepository extends JpaRepository<Score, Long> {
}
