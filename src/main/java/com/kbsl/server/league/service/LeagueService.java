package com.kbsl.server.league.service;

import com.kbsl.server.league.dto.request.LeagueSaveRequestDto;
import com.kbsl.server.league.dto.response.LeagueDeatilResponseDto;
import com.kbsl.server.league.dto.response.LeagueResponseDto;
import com.kbsl.server.league.enums.LeagueStatusType;
import com.kbsl.server.score.dto.response.ScoreResponseDto;
import org.springframework.data.domain.Page;

public interface LeagueService {
    LeagueResponseDto createLeague(LeagueSaveRequestDto leagueSaveRequestDto) throws Exception;

    Page<LeagueResponseDto> findLeagues(Integer page, LeagueStatusType leagueStatusType, String sort, Integer elementCnt) throws Exception;

    LeagueDeatilResponseDto findLeagueDetail(Long leagueSeq) throws Exception;

    Page<ScoreResponseDto> findLeagueSongScore(Long leagueSeq, Long songSeq, Integer page, String sort, Integer elementCnt) throws Exception;
}
