package com.kbsl.server.rank.service;

import com.kbsl.server.rank.dto.request.RankUpdateRequestDto;
import com.kbsl.server.rank.dto.response.RankResponseDto;
import com.kbsl.server.rank.enums.RankProcessType;
import org.springframework.data.domain.Page;

public interface RankService {
    RankResponseDto createSongRank(Long songSeq, String steamId) throws Exception;

    Page<RankResponseDto> findAllRank(Integer page, RankProcessType rankProcessType, String sort, Integer elementCnt) throws Exception;

    RankResponseDto updateRank(Long rankSeq, RankUpdateRequestDto rankUpdateRequestDto) throws Exception;
}
