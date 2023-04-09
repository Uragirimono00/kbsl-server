package com.kbsl.server.song.service;

import com.kbsl.server.song.dto.request.SongBadgeSaveRequestDto;
import com.kbsl.server.song.dto.response.SongBadgeResponseDto;

public interface SongBadgeService {
    SongBadgeResponseDto createBadge(SongBadgeSaveRequestDto songBadgeSaveRequestDto) throws Exception;
}
