package com.kbsl.server.league.service;

import com.kbsl.server.league.dto.request.LeagueSaveRequestDto;
import com.kbsl.server.league.dto.response.LeagueDeatilResponseDto;
import com.kbsl.server.league.dto.response.LeagueResponseDto;
import org.springframework.data.domain.Page;

public interface LeagueService {
    LeagueResponseDto createLeague(LeagueSaveRequestDto leagueSaveRequestDto) throws Exception;

    Page<LeagueResponseDto> findLeagues(Integer page, String sort, Integer elementCnt);

    LeagueDeatilResponseDto findLeagueDetail(Long leagueSeq);
}
