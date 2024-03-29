package com.kbsl.server.score.web;

import com.kbsl.server.score.dto.request.ScoreSaveRequestDto;
import com.kbsl.server.score.dto.response.ScoreResponseDto;
import com.kbsl.server.score.service.ScoreService;
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

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Tag(name = "Score", description = "점수 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/score")
public class ScoreController {

    private final ScoreService scoreService;

    @GetMapping(value = "/song/{songSeq}")
    @Tag(name = "Score")
    @Operation(summary = "[App] 점수 조회 API - Pagination",
        description =
            "노래 시퀀스를 전달받아 해당 노래의 점수를 조회한다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "노래 미조회")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<ScoreResponseDto>> findSongScore(
        @PathVariable Long songSeq,
        @RequestParam(value = "page") Integer page,
        @RequestParam(required = false, defaultValue = "latest | old | ...") String sort,
        @RequestParam(required = false, defaultValue = "10") Integer elementCnt
    ) throws Exception {
        return ResponseEntity.ok(scoreService.findSongScore(songSeq, page, sort, elementCnt));
    }

    @GetMapping(value = "/user/{userSeq}")
    @Tag(name = "Score")
    @Operation(summary = "[App] 점수 조회 API - Pagination",
            description =
                    "유저 시퀀스를 전달받아 해당 유저의 점수를 조회한다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "노래 미조회")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<ScoreResponseDto>> findUserScore(
            @PathVariable Long userSeq,
            @RequestParam(value = "page") Integer page,
            @RequestParam(required = false, defaultValue = "latest | old | ...") String sort,
            @RequestParam(required = false, defaultValue = "10") Integer elementCnt
    ) throws Exception {
        return ResponseEntity.ok(scoreService.findUserScore(userSeq, page, sort, elementCnt));
    }

    @GetMapping(value = "/adm/beatleader/song/{songSeq}")
    @Tag(name = "Score")
    @Operation(summary = "[App] 점수 조회 및 업데이트 API - BeatLeaderAPI, JWT사용",
        description =
            "노래 시퀀스를 전달받아 해당 노래의 점수를 조회 및 업데이트 한다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "노래 미조회")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> updateSongScoreFromBeatLeader(
        @PathVariable("songSeq") Long songSeq
    ) throws Exception {
        return ResponseEntity.ok(scoreService.updateSongScoreFromBeatLeader(songSeq));
    }

    @GetMapping(value = "/adm/beatleader/user/{userSeq}")
    @Tag(name = "Score")
    @Operation(summary = "[App] 특정 유저 점수 업데이트 및 조회 API - BeatLeaderAPI, JWT사용",
        description =
            "유저 시퀀스를 전달받아 해당 유저의 점수를 조회 및 업데이트 한다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "노래 미조회")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ScoreResponseDto> updateScoreFromBeatLeader(
        @PathVariable("userSeq") Long userSeq
    ) throws Exception {
        return ResponseEntity.ok(scoreService.updateScoreFromBeatLeader(userSeq));
    }

    @PostMapping(value = "")
    @Tag(name = "Score")
    @Operation(summary = "[App] 점수 제출 API",
        description =
            "요청자의 AccessToken을 이용하여 점수를 저장한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "점수 저장 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ScoreResponseDto> saveScore(
        @RequestBody ScoreSaveRequestDto scoreSaveRequestDto,
        HttpServletRequest httpServletRequest
        ) throws Exception {
        return ResponseEntity.ok(scoreService.saveScore(scoreSaveRequestDto, httpServletRequest.getRemoteAddr()));
    }
}
