package com.kbsl.server.score.service;

import com.kbsl.server.score.dto.response.ScoreResponseDto;
import org.springframework.data.domain.Page;

public interface ScoreService {
    Page<ScoreResponseDto> updateSongScore(Long songSeq, Integer page, String sort, Integer elementCnt) throws Exception;

    Page<ScoreResponseDto> findSongScore(Long songSeq, Integer page, String sort, Integer elementCnt) throws Exception;
}
