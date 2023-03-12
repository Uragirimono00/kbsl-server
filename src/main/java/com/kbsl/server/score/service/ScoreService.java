package com.kbsl.server.score.service;

import com.kbsl.server.user.dto.response.UserResponseDto;

public interface ScoreService {
    UserResponseDto updatePlayerScore(Long userSeq) throws Exception;
}
