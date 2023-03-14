package com.kbsl.server.score.domain.repository.custom;

import com.kbsl.server.score.domain.model.Score;
import com.querydsl.core.group.GroupBy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScoreRepositoryCustom {
    Page<Score> findAllScoreBySongSeqWithPage(Long songSeq, Pageable pageable, String sort);
}
