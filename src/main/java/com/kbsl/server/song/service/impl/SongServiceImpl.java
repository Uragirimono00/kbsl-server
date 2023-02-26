package com.kbsl.server.song.service.impl;

import com.kbsl.server.boot.exception.RestException;
import com.kbsl.server.league.domain.model.League;
import com.kbsl.server.league.domain.repository.LeagueRepository;
import com.kbsl.server.song.domain.model.Song;
import com.kbsl.server.song.domain.model.SongRepository;
import com.kbsl.server.song.dto.request.SongSaveRequestDto;
import com.kbsl.server.song.dto.response.SongResponseDto;
import com.kbsl.server.song.service.SongService;
import com.kbsl.server.user.domain.model.User;
import com.kbsl.server.user.domain.repository.UserRepository;
import com.kbsl.server.user.service.principal.PrincipalUserDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final UserRepository userRepository;
    private final LeagueRepository leagueRepository;
    private final SongRepository songRepository;

    @Override
    @Transactional
    public List<SongResponseDto> createSong(Long leagueSeq, List<SongSaveRequestDto> songSaveRequestDto) throws Exception {
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

        Integer success = 0;
        Integer failed = 0;

        List<Song> songEntityList = new ArrayList<>();
        List<SongResponseDto> songResponseDtoList = new ArrayList<>();
        Song songEntity = new Song();

        try {
            for(SongSaveRequestDto eachSong : songSaveRequestDto) {

                log.info(eachSong.toString());
                //엔티티에 추가할 노래 엔티티 생성
                songEntity = Song.builder()
                        .songId(eachSong.getSongId())
                        .songHash(eachSong.getSongHash())
                        .songDifficulty(eachSong.getSongDifficulty())
                        .songModeType(eachSong.getSongModeType())
                        .build();

                // 영속성 컨텍스트 업데이트
                songEntityList = leagueEntity.getSongsList();
                log.info(String.format("leagueSeq: %d, courseFileEntity: %s", leagueSeq, songEntityList.toString()));
                songEntityList.add(songEntity);

                // ResponseDto 생성
                songResponseDtoList.add(
                        SongResponseDto.builder()
                                .entity(songEntity)
                                .build()
                );
                success += 1;
            }
        }catch (Exception e){
            e.printStackTrace();
            failed += 1;
        }


        log.info(String.format("[%s - %d seq] 에 파일이 업로드 되었습니다. 성공: %d건, 실패: %d건", leagueEntity.getLeagueName(), leagueSeq, success, failed));

        return songResponseDtoList;
    }

    @Override
    public SongResponseDto findSong(Long songSeq) throws Exception {

        //실제로 노래가 존재하는지 검사
        Song songEntity = songRepository.findBySeq(songSeq)
                .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 노래를 찾을 수 없습니다. songSeq=" + songSeq));

        return SongResponseDto.builder().entity(songEntity).build();
    }
}
