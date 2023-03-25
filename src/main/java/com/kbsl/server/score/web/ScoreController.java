package com.kbsl.server.score.web;

import com.kbsl.server.league.dto.request.LeagueSaveRequestDto;
import com.kbsl.server.league.dto.response.LeagueResponseDto;
import com.kbsl.server.score.dto.request.ScoreSaveRequestDto;
import com.kbsl.server.score.dto.response.ScoreResponseDto;
import com.kbsl.server.score.service.ScoreService;
import com.kbsl.server.song.dto.request.SongSaveRequestDto;
import com.kbsl.server.song.dto.response.SongResponseDto;
import com.kbsl.server.user.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Score", description = "점수 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/score")
public class ScoreController {

    private final ScoreService scoreService;

    @GetMapping(value = "")
    @Tag(name = "Score")
    @Operation(summary = "[App] 점수 조회 API - Pagination",
            description =
                    "노래 시퀀스를 전달받아 해당 노래에 점수를 조회한다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "과정 생성 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "리그 미조회")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<ScoreResponseDto>> findSongScore(
            @RequestParam("songSeq") Long songSeq,
            @RequestParam(value = "page") Integer page,
            @RequestParam(required = false, defaultValue = "latest | old | ...") String sort,
            @RequestParam(required = false, defaultValue = "10") Integer elementCnt
    ) throws Exception {
        return ResponseEntity.ok(scoreService.findSongScore(songSeq, page, sort, elementCnt));
    }

    @GetMapping(value = "/beatleader/adm")
    @Tag(name = "Score")
    @Operation(summary = "[App] 점수 조회 및 업데이트 API - BeatLeaderAPI, JWT사용",
        description =
            "노래 시퀀스를 전달받아 해당 노래에 점수를 조회 및 업데이트 한다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "과정 생성 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "리그 미조회")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<ScoreResponseDto>> updateSongScore(
        @RequestParam("songSeq") Long songSeq,
        @RequestParam(value = "page") Integer page,
        @RequestParam(required = false, defaultValue = "latest | old | ...") String sort,
        @RequestParam(required = false, defaultValue = "10") Integer elementCnt
    ) throws Exception {
        return ResponseEntity.ok(scoreService.updateSongScore(songSeq, page, sort, elementCnt));
    }

    @PostMapping(value = "")
    @Tag(name = "Score")
    @Operation(summary = "[App] 점수 제출 API",
            description =
                    "요청자의 steamId를 이용하여 점수를 저장한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "점수 저장 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ScoreResponseDto> saveScoreWithSteamId(
            @RequestBody ScoreSaveRequestDto scoreSaveRequestDto
    ) throws Exception {
        return ResponseEntity.ok(scoreService.saveScoreWithSteamId(scoreSaveRequestDto));
    }
}
