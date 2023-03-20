package com.kbsl.server.score.domain.repository.custom;

import com.kbsl.server.score.domain.model.Score;
import com.querydsl.core.group.GroupBy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ScoreRepositoryCustom {
    Page<Score> findAllScoreBySongSeqWithPage(Long songSeq, Pageable pageable, String sort);

    Page<Score> findAllScoreBySongSeqAndLeagueDateWithPage(Long songSeq, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, String sort);
}
