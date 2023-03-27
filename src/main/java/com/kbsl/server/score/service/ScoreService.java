package com.kbsl.server.score.service;

import com.kbsl.server.score.dto.request.ScoreSaveRequestDto;
import com.kbsl.server.score.dto.response.ScoreResponseDto;
import org.springframework.data.domain.Page;

public interface ScoreService {
    Boolean updateSongScoreFromBeatLeader(Long songSeq) throws Exception;

    Page<ScoreResponseDto> findSongScore(Long songSeq, Integer page, String sort, Integer elementCnt) throws Exception;

    ScoreResponseDto saveScoreWithSteamId(ScoreSaveRequestDto scoreSaveRequestDto) throws Exception;

    ScoreResponseDto updateScoreFromBeatLeader(Long userSeq) throws Exception;
}
