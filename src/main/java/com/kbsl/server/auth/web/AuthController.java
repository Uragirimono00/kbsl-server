package com.kbsl.server.auth.web;

import com.kbsl.server.auth.dto.request.AccessTokenRefreshTokenDto;
import com.kbsl.server.auth.dto.response.AccessTokenRefreshResponseDto;
import com.kbsl.server.auth.dto.response.AuthLoginResponse;
import com.kbsl.server.auth.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Api(tags = {"[App & 관리자] 인증 API"})
public class AuthController {
    private final AuthService authService;

    @GetMapping("discord")
    @ApiOperation(
            value = "Discord 로그인 API",
            notes = "Discord 에서 받은 인가코드를 이용해서 로그인을 수행한다. <br>" +
                    " OAuth2를 사용해 로그인할 때는 해당 주소로 연결해야한다. <br>" +
                    "<b><a href='https://localhost:8080/api/v1/oauth2/authorization/discord'> https://localhost:8080/api/v1/oauth2/authorization/discord </a></b> <br>" +
                    " Dev : " +
                    " Prod : "
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "로그인 성공", response = AuthLoginResponse.class),
            @ApiResponse(code = 401, message = "로그인 실패")
    })
    public ResponseEntity<AuthLoginResponse> signInDiscord(@RequestParam String code, HttpServletRequest request) {
        return new ResponseEntity<>(authService.authLogin(code, request.getServletPath()), HttpStatus.OK);
    }

    @PostMapping("token/refresh")
    @ApiOperation(
            value = "AccessToken Refresh 토크으로 새로고침 하기",
            notes = "기존에 진행한 로그인에서 받은 AccessToken과 RefreshToken을 이용해서 AccessToken을 새로고침한다."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "새로고침 성공", response = AccessTokenRefreshResponseDto.class),
            @ApiResponse(code = 401, message = "새로고침 실패")
    })
    public ResponseEntity<AccessTokenRefreshResponseDto> accessTokenRefresh(@RequestBody AccessTokenRefreshTokenDto requestDto) {
        return new ResponseEntity<>(authService.accessTokenRefresh(requestDto), HttpStatus.OK);
    }

    @GetMapping("logOut")
    @ApiOperation(
            value = "로그아웃 API",
            notes = "로그인해서 받은 AccessToken을 이용해 로그아웃을 수행한다."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "로그아웃 성공", response = Boolean.class),
            @ApiResponse(code = 401, message = "로그아웃 실패 실패")
    })
    public ResponseEntity<Boolean> logOut(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String accessToken) {
        return new ResponseEntity<>(authService.logOut(accessToken), HttpStatus.OK);
    }
}

