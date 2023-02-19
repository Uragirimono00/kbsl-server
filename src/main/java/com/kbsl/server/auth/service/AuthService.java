package com.kbsl.server.auth.service;

import com.kbsl.server.auth.dto.request.AccessTokenRefreshTokenDto;
import com.kbsl.server.auth.dto.response.AccessTokenRefreshResponseDto;
import com.kbsl.server.auth.dto.response.AuthLoginResponse;

public interface AuthService {
    AuthLoginResponse authLogin(String code, String requestPath);
    String getUserName();
    AccessTokenRefreshResponseDto accessTokenRefresh(AccessTokenRefreshTokenDto requestDto);
    Boolean logOut(String accessToken);
}