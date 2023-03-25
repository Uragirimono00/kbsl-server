package com.kbsl.server.song.web;

import com.kbsl.server.song.dto.request.SongSaveRequestDto;
import com.kbsl.server.song.dto.response.SongApiResponseDto;
import com.kbsl.server.song.dto.response.SongResponseDto;
import com.kbsl.server.song.service.SongService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
public class SongController {
    private final SongService songService;

    @PostMapping(value = "/{leagueSeq}")
    @Tag(name = "Song")
    @Operation(summary = "[App] 리그 안에 노래 추가 API",
        description =
            "리그 시퀀스를 Path Variable 로 전달받아 해당 리그의 노래를 추가한다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "과정 생성 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "리그 미조회")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<SongResponseDto>> createLeagueSong(
        @PathVariable("leagueSeq") Long leagueSeq,
        @RequestBody List<SongSaveRequestDto> songSaveRequestDto
    ) throws Exception {
        return ResponseEntity.ok(songService.createLeagueSong(leagueSeq, songSaveRequestDto));
    }

    @GetMapping(value = "/adm/{songSeq}")
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

    @GetMapping(value = "/beatsaver/id/{id}")
    @Tag(name = "Song")
    @Operation(summary = "[App] 노래 조회 API - BeatSaverAPI",
        description =
            "Song Id를 Path Variable 로 전달받아 해당 노래를 조회한다." +
                "만약 DB안에 노래가 없을경우 BeatSaver조회하여 DB에 저장시킨뒤 곡을 보여준다." +
                "단, 노래가 조회되지않을 경우 예외를 발생시킨다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "조회된 노래 없음")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<SongApiResponseDto>> findSongById(
        @PathVariable String id
    ) throws Exception {
        return ResponseEntity.ok(songService.findSongById(id));
    }

    @GetMapping(value = "/beatsaver/hash/{hash}")
    @Tag(name = "Song")
    @Operation(summary = "[App] 노래 조회 API - BeatSaverAPI",
        description =
            "Song Hash를 Path Variable 로 전달받아 해당 노래를 조회한다." +
                "만약 DB안에 노래가 없을경우 BeatSaver조회하여 DB에 저장시킨뒤 곡을 보여준다." +
                "단, 노래가 조회되지않을 경우 예외를 발생시킨다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "조회된 노래 없음")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<SongApiResponseDto>> findSongByHash(
        @PathVariable String hash
    ) throws Exception {
        return ResponseEntity.ok(songService.findSongByHash(hash));
    }

}
