package com.kbsl.server.auth.web;

import com.kbsl.server.auth.dto.request.AccessTokenRefreshTokenDto;
import com.kbsl.server.auth.dto.request.SteamLoginRequestDto;
import com.kbsl.server.auth.dto.response.AccessTokenRefreshResponseDto;
import com.kbsl.server.auth.dto.response.AuthLoginResponse;
import com.kbsl.server.auth.service.AuthService;
import com.kbsl.server.song.dto.request.SongSaveRequestDto;
import com.kbsl.server.song.dto.response.SongResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth", description = "인증 API")
public class AuthController {
    private final AuthService authService;

    @GetMapping("discord")
    @Tag(name = "Auth")
    @Operation(
            summary = "Discord 로그인 API",
            description = "Discord 에서 받은 인가코드를 이용해서 로그인을 수행한다. <br>" +
                    " OAuth2를 사용해 로그인할 때는 해당 주소로 연결해야한다. <br>" +
                    " Local : <b><a target='_blank' href='https://discord.com/api/oauth2/authorize?client_id=1072886733156384929&redirect_uri=http%3A%2F%2Flocalhost%3A8090%2Fauth%2Fdiscord&response_type=code&scope=identify'> http://localhost:8090/api/v1/auth/discord </a></b> <br>" +
                    " Dev : <b><a target='_blank' href='https://discord.com/api/oauth2/authorize?client_id=1072886733156384929&redirect_uri=http%3A%2F%2F52.79.222.211%3A8090%2Fauth%2Fdiscord&response_type=code&scope=identify'> http://52.79.222.211:8090/auth/discord </a></b> <br>" +
                    " Prod : "
    )
    @ApiResponses( {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 실패")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AuthLoginResponse> signInDiscord(
        @RequestParam String code,
        HttpServletRequest request) {
        return new ResponseEntity<>(authService.authLogin(code, request.getServletPath()), HttpStatus.OK);
    }

    @GetMapping("steam")
    @Tag(name = "Auth")
    @Operation(
        summary = "Steam 로그인 API",
        description = "Steam 에서 받은 세션Id를 이용해서 로그인을 수행한다. <br>"
    )
    @ApiResponses( {
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "401", description = "로그인 실패")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AuthLoginResponse> authSteam(
            @RequestParam String ticket,
        HttpServletRequest request
    ) throws Exception {
        return ResponseEntity.ok(authService.authSteam(ticket));
    }

    @PostMapping("token/refresh")
    @Tag(name = "Auth")
    @Operation(
            summary = "AccessToken Refresh 토크으로 새로고침 하기",
            description = "기존에 진행한 로그인에서 받은 AccessToken과 RefreshToken을 이용해서 AccessToken을 새로고침한다."
    )
    @ApiResponses( {
            @ApiResponse(responseCode = "201", description = "리프레쉬 성공"),
            @ApiResponse(responseCode = "401", description = "리프레쉬 실패")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AccessTokenRefreshResponseDto> accessTokenRefresh(
        @RequestBody AccessTokenRefreshTokenDto requestDto) {
        return new ResponseEntity<>(authService.accessTokenRefresh(requestDto), HttpStatus.OK);
    }

    @GetMapping("logOut")
    @Tag(name = "Auth")
    @Operation(
            summary = "로그아웃 API",
            description = "로그인해서 받은 AccessToken을 이용해 로그아웃을 수행한다."
    )
    @ApiResponses( {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "로그아웃 실패")
    })
    public ResponseEntity<Boolean> logOut(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String accessToken) {
        return new ResponseEntity<>(authService.logOut(accessToken), HttpStatus.OK);
    }

}

