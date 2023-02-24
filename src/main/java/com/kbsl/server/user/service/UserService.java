package com.kbsl.server.user.service;

import com.kbsl.server.user.dto.request.UserUpdateRequestDto;
import com.kbsl.server.user.dto.response.UserResponseDto;

public interface UserService {
    UserResponseDto findDetailUser(Long userSeq);

    UserResponseDto updateUser(Long userSeq, UserUpdateRequestDto userUpdateRequestDto);
}
