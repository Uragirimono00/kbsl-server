package com.kbsl.server.score.service.impl;

import com.kbsl.server.boot.exception.RestException;
import com.kbsl.server.boot.util.BeatLeaderUtils;
import com.kbsl.server.league.domain.repository.LeagueRepository;
import com.kbsl.server.score.domain.model.Score;
import com.kbsl.server.score.domain.repository.ScoreRepository;
import com.kbsl.server.score.dto.request.ScoreSaveRequestDto;
import com.kbsl.server.score.dto.response.ScoreResponseDto;
import com.kbsl.server.score.service.ScoreService;
import com.kbsl.server.song.domain.model.Song;
import com.kbsl.server.song.domain.repository.SongRepository;
import com.kbsl.server.user.domain.model.User;
import com.kbsl.server.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.kbsl.server.boot.util.BeatSaverUtils.saveSongByHashFromBeatSaverAPI;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {

    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final ScoreRepository scoreRepository;

    @Override
    @Transactional
    public Page<ScoreResponseDto> updateSongScore(Long songSeq, Integer page, String sort, Integer elementCnt) throws Exception {

        Song songEntity = songRepository.findBySeq(songSeq)
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 곡을 찾을 수 없습니다."));

        /**
         * 유저 정보를 가져온 후, DTO 에 삽입한다.
         */
        List<User> users = userRepository.findBySteamIdIsNotNull();
        for (User user : users) {
            BeatLeaderUtils.saveScoreFromBeatLeaderAPI(user, songEntity, scoreRepository);
        }

        /**
         * 페이징 객체를 생성한다.
         */
        Pageable pageable = PageRequest.of(page, elementCnt == null ? 10 : elementCnt);

        return scoreRepository.findAllScoreBySongSeqWithPage(songSeq, pageable, sort)
            .map(score -> ScoreResponseDto.builder().entity(score).build());

    }

    @Override
    @Transactional
    public Page<ScoreResponseDto> findSongScore(Long songSeq, Integer page, String sort, Integer elementCnt) throws Exception {
        Song songEntity = songRepository.findBySeq(songSeq)
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 곡을 찾을 수 없습니다."));

        // 페이징 객체를 생성한다.
        Pageable pageable = PageRequest.of(page, elementCnt == null ? 10 : elementCnt);

        return scoreRepository.findAllScoreBySongSeqWithPage(songSeq, pageable, sort)
            .map(score -> ScoreResponseDto.builder().entity(score).build());
    }

    @Override
    @Transactional
    public ScoreResponseDto saveScoreWithSteamId(ScoreSaveRequestDto requestDto) throws Exception {

        User userEntity = userRepository.findBySteamId(requestDto.getSteamId())
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 유저를 찾을 수 없습니다. steamId = " + requestDto.getSteamId()));

        Song songEntity = songRepository.findBySongModeTypeAndSongHashAndSongDifficulty(requestDto.getSongModeType(), requestDto.getSongHash(), requestDto.getSongDifficulty());

        if (songEntity == null) {
            saveSongByHashFromBeatSaverAPI(requestDto.getSongHash(), songRepository);
            // 노래 재 조회
            songEntity = songRepository.findBySongModeTypeAndSongHashAndSongDifficulty(requestDto.getSongModeType(), requestDto.getSongHash(), requestDto.getSongDifficulty());
            if (songEntity == null) {
                // todo: 스코어 기록이 실패할 경우 log에 해당기록을 남겨 스코어를 잃어버리지 않도록 예외를 처리한다.
                throw new RestException(HttpStatus.NOT_FOUND, "노래를 찾을 수 없습니다... 재시도 해주십시오...");
            }
        }

        // 점수를 저장한다.
        Score score = Score.builder()
            .user(userEntity)
            .song(songEntity)
            .scoreSeq(0L)
            .baseScore(requestDto.getBaseScore())
            .modifiedScore(requestDto.getModifiedScore())
            .accuracy(requestDto.getAccuracy())
            .badCut(requestDto.getBadCut())
            .missedNote(requestDto.getMissedNote())
            .bombCut(requestDto.getBombCut())
            .wallsHit(requestDto.getWallsHit())
            .pause(requestDto.getPause())
            .playCount(requestDto.getPlayCount())
            .accLeft(requestDto.getAccLeft())
            .accRight(requestDto.getAccRight())
            .comment("")
            .timePost(requestDto.getTimePost())
            .build();

        scoreRepository.save(score);

        return ScoreResponseDto.builder().entity(score).build();
    }
}
