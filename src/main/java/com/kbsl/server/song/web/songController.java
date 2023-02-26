package com.kbsl.server.song.web;

import com.kbsl.server.league.dto.request.LeagueSaveRequestDto;
import com.kbsl.server.league.dto.response.LeagueResponseDto;
import com.kbsl.server.league.service.LeagueService;
import com.kbsl.server.song.dto.request.SongSaveRequestDto;
import com.kbsl.server.song.dto.response.SongResponseDto;
import com.kbsl.server.song.service.SongService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Song", description = "노래 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/song")
public class songController {
    private final SongService songService;

    @PostMapping(value = "/{leagueSeq}")
    @Tag(name = "Song")
    @Operation(summary = "[App] 노래 생성 API",
            description =
                    "리그 시퀀스를 Path Variable 로 전달받아 해당 리그의 노래를 추가한다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "과정 생성 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "리그 미조회")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<SongResponseDto>> createSong(
            @PathVariable("leagueSeq") Long leagueSeq,
            @RequestBody List<SongSaveRequestDto> songSaveRequestDto
    ) throws Exception {
        return ResponseEntity.ok(songService.createSong(leagueSeq, songSaveRequestDto));
    }

    @GetMapping(value = "/{songSeq}")
    @Tag(name = "Song")
    @Operation(summary = "[App] 노래 단건 조회 API",
            description =
                    "노래 시퀀스를 Path Variable 로 전달받아 해당 노래를 수정한다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "조회된 노래 없음")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<SongResponseDto> findSong(
            @PathVariable Long songSeq
    ) throws Exception {
        return ResponseEntity.ok(songService.findSong(songSeq));
    }

}
