package com.kbsl.server.song.service;

import com.kbsl.server.song.dto.request.SongSaveRequestDto;
import com.kbsl.server.song.dto.response.SongApiResponseDto;
import com.kbsl.server.song.dto.response.SongResponseDto;

import java.util.List;

public interface SongService {
    List<SongResponseDto> createSong(Long leagueSeq, List<SongSaveRequestDto> songSaveRequestDto) throws Exception;

    SongResponseDto findSong(Long songSeq) throws Exception;

    List<SongApiResponseDto> findIdApi(String id) throws Exception;
}
