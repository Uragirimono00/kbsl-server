package com.kbsl.server.league.web;

import com.kbsl.server.league.dto.request.LeagueSaveRequestDto;
import com.kbsl.server.league.dto.response.LeagueDeatilResponseDto;
import com.kbsl.server.league.dto.response.LeagueResponseDto;
import com.kbsl.server.league.enums.LeagueStatusType;
import com.kbsl.server.league.service.LeagueService;
import com.kbsl.server.score.dto.response.ScoreResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Tag(name = "League", description = "리그 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/league")
public class LeagueController {
    private final LeagueService leagueService;

    @PostMapping(value = "")
    @Tag(name = "League")
    @Operation(summary = "[App] 리그 생성 API",
        description =
            "요청자의 Access Token을 이용하여 리그를 생성한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "리그 생성 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<LeagueResponseDto> createPost(
        @RequestParam("steamId") String steamId,
        @RequestBody LeagueSaveRequestDto leagueSaveRequestDto
    ) throws Exception {
        return ResponseEntity.ok(leagueService.createLeague(steamId, leagueSaveRequestDto));
    }

    @GetMapping(value = "")
    @Tag(name = "League")
    @Operation(summary = "[App] 리그 전체 조회 API - Pagination",
        description =
            "모든 리그 정보를 elementCnt 개수 만큼 조회한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<LeagueResponseDto>> findLeagues(
        @RequestParam(value = "page") Integer page,
        @RequestParam(value = "leagueStatusType") LeagueStatusType leagueStatusType,
        @RequestParam(required = false, defaultValue = "latest | old | ...") String sort,
        @RequestParam(required = false, defaultValue = "10") Integer elementCnt
    ) throws Exception {
        return ResponseEntity.ok(leagueService.findLeagues(page, leagueStatusType, sort, elementCnt));
    }

    @GetMapping(value = "/{leagueSeq}")
    @Tag(name = "League")
    @Operation(summary = "[App] 리그 상세 조회 API",
        description =
            "리그 시퀀스를 Path Variable 로 전달받아 해당 리그를 조회한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<LeagueDeatilResponseDto> findLeagueDetail(
        @PathVariable(value = "leagueSeq") Long leagueSeq
    ) throws Exception {
        return ResponseEntity.ok(leagueService.findLeagueDetail(leagueSeq));
    }

    @GetMapping(value = "/{leagueSeq}/{songSeq}")
    @Tag(name = "League")
    @Operation(summary = "[App] 리그 노래 스코어 조회 API",
        description =
            "리그 시퀀스와 노래 시퀀스를 Path Variable 로 전달받아 해당 리그의 노래의 스코어를 조회한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<ScoreResponseDto>> findLeagueSongScore(
        @PathVariable(value = "leagueSeq") Long leagueSeq,
        @PathVariable(value = "songSeq") Long songSeq,
        @RequestParam(value = "page") Integer page,
        @RequestParam(required = false, defaultValue = "latest | old | ...") String sort,
        @RequestParam(required = false, defaultValue = "10") Integer elementCnt
    ) throws Exception {
        return ResponseEntity.ok(leagueService.findLeagueSongScore(leagueSeq, songSeq, page, sort, elementCnt));
    }
}
