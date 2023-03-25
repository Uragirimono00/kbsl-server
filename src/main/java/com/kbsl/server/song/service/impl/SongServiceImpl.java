package com.kbsl.server.song.service.impl;

import com.kbsl.server.boot.exception.RestException;
import com.kbsl.server.league.domain.model.League;
import com.kbsl.server.league.domain.repository.LeagueRepository;
import com.kbsl.server.song.domain.model.Song;
import com.kbsl.server.song.domain.repository.SongRepository;
import com.kbsl.server.song.dto.request.SongSaveRequestDto;
import com.kbsl.server.song.dto.response.SongApiResponseDto;
import com.kbsl.server.song.dto.response.SongResponseDto;
import com.kbsl.server.song.enums.SongDifficultyType;
import com.kbsl.server.song.enums.SongModeType;
import com.kbsl.server.song.service.SongService;
import com.kbsl.server.user.domain.model.User;
import com.kbsl.server.user.domain.repository.UserRepository;
import com.kbsl.server.user.service.principal.PrincipalUserDetail;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.JSONValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.kbsl.server.boot.util.BeatSaverUtils.saveSongByHashFromBeatSaverAPI;
import static com.kbsl.server.boot.util.BeatSaverUtils.saveSongByIdFromBeatSaverAPI;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final UserRepository userRepository;
    private final LeagueRepository leagueRepository;
    private final SongRepository songRepository;

    @Override
    @Transactional
    public List<SongResponseDto> createLeagueSong(Long leagueSeq, List<SongSaveRequestDto> songSaveRequestDto) throws Exception {
        /**
         * 유저 정보를 가져온 후, DTO 에 삽입한다.
         */
        PrincipalUserDetail userDetails = (PrincipalUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        /**
         * 유저 및 리그에 대한 정보 및 추가 정보를 수정한다.
         */
        User userEntity = userRepository.findBySeq(userDetails.getUserSeq())
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 유저를 찾을 수 없습니다."));

        League leagueEntity = leagueRepository.findBySeq(leagueSeq)
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 리그를 찾을 수 없습니다. leagueSeq=" + leagueSeq));

        List<SongResponseDto> songResponseDtoList = new ArrayList<>();

        for (SongSaveRequestDto eachSong : songSaveRequestDto) {
            log.info(eachSong.toString());

            Song alreadySongEntity = songRepository.findBySongModeTypeAndSongHashAndSongDifficulty(eachSong.getSongModeType(), eachSong.getSongHash(), eachSong.getSongDifficulty());

            if (alreadySongEntity == null){
                saveSongByHashFromBeatSaverAPI(eachSong.getSongHash(), songRepository);
            }
            Song songEntity = songRepository.findBySongModeTypeAndSongHashAndSongDifficulty(eachSong.getSongModeType(), eachSong.getSongHash(), eachSong.getSongDifficulty());

            leagueEntity.getSongsList().add(songEntity);

            songResponseDtoList.add(SongResponseDto.builder().entity(songEntity).build());
        }

        log.info(String.format("[%s - %d seq] 에 파일이 업로드 되었습니다. 성공: %d건, 실패: %d건",
            leagueEntity.getLeagueName(), leagueSeq, songSaveRequestDto.size(), 0));

        return songResponseDtoList;
    }


    @Override
    @Transactional
    public SongResponseDto findSong(Long songSeq) throws Exception {

        //실제로 노래가 존재하는지 검사
        Song songEntity = songRepository.findBySeq(songSeq)
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 노래를 찾을 수 없습니다. songSeq=" + songSeq));

        return SongResponseDto.builder().entity(songEntity).build();
    }

    @Override
    @Transactional
    public List<SongApiResponseDto> findIdApi(String id) throws Exception {

        List<SongApiResponseDto> songApiResponseDtoArrayList = saveSongByIdFromBeatSaverAPI(id, songRepository);

        return songApiResponseDtoArrayList;
    }

}
