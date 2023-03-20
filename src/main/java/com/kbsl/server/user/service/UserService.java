package com.kbsl.server.user.service;

import com.kbsl.server.user.dto.request.UserUpdateRequestDto;
import com.kbsl.server.user.dto.response.UserResponseDto;

public interface UserService {
    UserResponseDto findDetailUser(Long userSeq) throws Exception;

    UserResponseDto updateSteamIdWithBeatLeader(Long userSeq, UserUpdateRequestDto userUpdateRequestDto) throws Exception;

    UserResponseDto findAtInfo(String authorization) throws Exception;

    UserResponseDto updateSteamId(Long userSeq, UserUpdateRequestDto userUpdateRequestDto) throws Exception;
}
