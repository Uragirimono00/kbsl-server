package com.kbsl.server.rank.web;

import com.kbsl.server.league.enums.LeagueStatusType;
import com.kbsl.server.rank.dto.response.RankResponseDto;
import com.kbsl.server.rank.enums.RankProcessType;
import com.kbsl.server.rank.service.RankService;
import com.kbsl.server.song.dto.request.SongSaveRequestDto;
import com.kbsl.server.song.dto.response.SongResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Rank", description = "랭크 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/rank")
public class RankController {

    private final RankService rankService;

    @PostMapping(value = "")
    @Tag(name = "Rank")
    @Operation(summary = "[App] 랭크 지명 API",
        description =
            "노래 시퀀스를 Path Variable 로 전달받아 랭크로 지명한다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "생성 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "노래 미조회")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RankResponseDto> createSongRank(
        @RequestParam("songSeq") Long songSeq,
        @RequestParam("steamId") String steamId

    ) throws Exception {
        return ResponseEntity.ok(rankService.createSongRank(songSeq, steamId));
    }

    @GetMapping(value = "")
    @Tag(name = "Rank")
    @Operation(summary = "[App] 랭크 전체 조회 API - Pagination",
        description =
            "모든 랭크를 조회한다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "노래 미조회")
    })
    public ResponseEntity<Page<RankResponseDto>> findAllRank(
        @RequestParam(value = "page") Integer page,
        @RequestParam(value = "rankProcessType") RankProcessType rankProcessType,
        @RequestParam(required = false, defaultValue = "latest | old | ...") String sort,
        @RequestParam(required = false, defaultValue = "10") Integer elementCnt
    ) throws Exception {
        return ResponseEntity.ok(rankService.findAllRank(page, rankProcessType, sort, elementCnt));
    }

    @PutMapping(value = "/{rankSeq}")
    @Tag(name = "Rank")
    @Operation(summary = "[App] 랭크 수정 API",
        description =
            "rankSeq를 PathVariable로 받아 현재 랭크상태를 수정한다. <br>" +
                "단 RT권한이 없거나 관리자가 아닐경우 예외를 발생시킨다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "노래 미조회")
    })
    public ResponseEntity<RankResponseDto> updateRank(
        @PathVariable(value = "rankSeq") Long rankSeq,
        @RequestParam("steamId") String steamId
    ) throws Exception {
        return ResponseEntity.ok(rankService.updateRank(rankSeq, steamId));
    }
}
