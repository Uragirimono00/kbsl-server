package com.kbsl.server.score.service.impl;

import com.kbsl.server.score.domain.repository.ScoreRepository;
import com.kbsl.server.score.service.ScoreService;
import com.kbsl.server.user.dto.response.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {

    private final ScoreRepository scoreRepository;


    @Override
    @Transactional
    public UserResponseDto updatePlayerScore(Long userSeq) throws Exception {
        return null;
    }
}
