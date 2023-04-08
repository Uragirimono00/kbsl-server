package com.kbsl.server.test.web;

import com.kbsl.server.song.dto.response.SongResponseDto;
import com.kbsl.server.test.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Test", description = "테스트 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final TestService testService;

    @GetMapping(value = "/discord/embed")
    @Tag(name = "Test")
    @Operation(summary = "[App] 디코 테스트 API",
            description =
                    "테스트"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "조회된 노래 없음")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> discordEmbedTest(
    ) throws Exception {
        return ResponseEntity.ok(testService.discordEmbedTest());
    }
}
