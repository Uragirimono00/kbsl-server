package com.kbsl.server.song.service;

import com.kbsl.server.song.dto.request.SongSaveRequestDto;
import com.kbsl.server.song.dto.response.SongApiResponseDto;
import com.kbsl.server.song.dto.response.SongResponseDto;

import java.util.List;

public interface SongService {
    List<SongResponseDto> createLeagueSong(Long leagueSeq, List<SongSaveRequestDto> songSaveRequestDto) throws Exception;

    SongResponseDto findSong(Long songSeq) throws Exception;

    List<SongApiResponseDto> findSongById(String id) throws Exception;

    List<SongApiResponseDto> findSongByHash(String hash) throws Exception;
}
