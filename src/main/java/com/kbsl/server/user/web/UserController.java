package com.kbsl.server.user.web;

import com.kbsl.server.user.dto.request.UserUpdateRequestDto;
import com.kbsl.server.user.dto.response.UserResponseDto;
import com.kbsl.server.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "User", description = "유저 API")
public class UserController {
    private final UserService userService;

    @GetMapping("/{userSeq}")
    @Tag(name = "User")
    @Operation(summary = "유저 조회 API",
            description =
                    "유저 시퀀스를 받아 해당 유저를 조회한다."
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponseDto> findUser(
            @PathVariable(value = "userSeq") Long userSeq
    ) throws Exception {
        return ResponseEntity.ok(userService.findDetailUser(userSeq));
    }

    @PutMapping(value = "/{userSeq}")
    @Tag(name = "User")
    @Operation(summary = "유저 비트리더ID 수정 API",
            description =
                    "앱에서 유저 시퀀스를 Path Variable 로 전달받아 해당 유저를 수정한다. 이때, 요청자는 작성자의 유저 시퀀스와 일치해야한다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "유저 수정 성공"),
            @ApiResponse(responseCode = "403", description = "수정 실패 - 요청자와 작성자 미일치"),
            @ApiResponse(responseCode = "404", description = "수정 실패 - 유저 미조회")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable("userSeq") Long userSeq,
            @RequestBody UserUpdateRequestDto userUpdateRequestDto
    ) throws Exception {
        return ResponseEntity.ok(userService.updateUser(userSeq, userUpdateRequestDto));
    }
}
