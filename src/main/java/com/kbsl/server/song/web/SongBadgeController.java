package com.kbsl.server.song.web;

import com.kbsl.server.song.dto.request.SongBadgeSaveRequestDto;
import com.kbsl.server.song.dto.response.SongBadgeResponseDto;
import com.kbsl.server.song.service.SongBadgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Badge", description = "배지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/badge")
public class SongBadgeController {
    private final SongBadgeService songBadgeService;

    @PostMapping(value = "/adm")
    @Tag(name = "Badge")
    @Operation(summary = "[App] 배지 추가 API",
            description =
                    "배지 목록을 추가하는 API"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "미조회")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<SongBadgeResponseDto> createBadge(
            @RequestBody SongBadgeSaveRequestDto songBadgeSaveRequestDto
    ) throws Exception {
        return ResponseEntity.ok(songBadgeService.createBadge(songBadgeSaveRequestDto));
    }
}
