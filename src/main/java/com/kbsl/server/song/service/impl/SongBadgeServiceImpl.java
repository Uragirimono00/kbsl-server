package com.kbsl.server.song.service.impl;

import com.kbsl.server.boot.exception.RestException;
import com.kbsl.server.song.domain.model.SongBadge;
import com.kbsl.server.song.domain.repository.SongBadgeRepository;
import com.kbsl.server.song.dto.request.SongBadgeSaveRequestDto;
import com.kbsl.server.song.dto.response.SongBadgeResponseDto;
import com.kbsl.server.song.service.SongBadgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SongBadgeServiceImpl implements SongBadgeService {
    private final SongBadgeRepository songBadgeRepository;

    /**
     * 배지 생성 API
     * 단, 이미 존재하는 배지타입의 경우 예외를 발생시킨다.
     * @param songBadgeSaveRequestDto
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public SongBadgeResponseDto createBadge(SongBadgeSaveRequestDto songBadgeSaveRequestDto) throws Exception {

        // 이미 존재하는 배지의 경우 예외를 발생시킨다.
        if (songBadgeRepository.findBySongBadgeType(songBadgeSaveRequestDto.getSongBadgeType()).isPresent()){
            throw new RestException(HttpStatus.BAD_REQUEST, "이미 존재하는 배지입니다. BadgeType = " + songBadgeSaveRequestDto.getSongBadgeType());
        }

        // ResponseDto를 생성한다.
        SongBadge songBadgeEntity = songBadgeRepository.save(songBadgeSaveRequestDto.toEntity());

        return SongBadgeResponseDto.builder().entity(songBadgeEntity).build();
    }
}
