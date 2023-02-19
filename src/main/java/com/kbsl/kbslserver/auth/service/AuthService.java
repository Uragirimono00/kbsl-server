package com.kbsl.kbslserver.auth.service;

import com.kbsl.kbslserver.auth.dto.request.AccessTokenRefreshTokenDto;
import com.kbsl.kbslserver.auth.dto.response.AccessTokenRefreshResponseDto;
import com.kbsl.kbslserver.auth.dto.response.AuthLoginResponse;

public interface AuthService {
    AuthLoginResponse authLogin(String code, String requestPath);
    String getUserName();
    AccessTokenRefreshResponseDto accessTokenRefresh(AccessTokenRefreshTokenDto requestDto);
    Boolean logOut(String accessToken);
}