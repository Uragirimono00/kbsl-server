package com.kbsl.server.score.service.impl;

import com.kbsl.server.boot.discord.Author;
import com.kbsl.server.boot.discord.DiscordEmbed;
import com.kbsl.server.boot.discord.DiscordMessage;
import com.kbsl.server.boot.exception.RestException;
import com.kbsl.server.boot.util.BeatLeaderUtils;
import com.kbsl.server.boot.util.BeatSaverUtils;
import com.kbsl.server.boot.util.DiscordUtils;
import com.kbsl.server.score.domain.model.Score;
import com.kbsl.server.score.domain.repository.ScoreRepository;
import com.kbsl.server.score.dto.request.ScoreSaveRequestDto;
import com.kbsl.server.score.dto.response.ScoreResponseDto;
import com.kbsl.server.score.service.ScoreService;
import com.kbsl.server.song.domain.model.Song;
import com.kbsl.server.song.domain.repository.SongRepository;
import com.kbsl.server.user.domain.model.User;
import com.kbsl.server.user.domain.repository.UserRepository;
import com.kbsl.server.user.service.principal.PrincipalUserDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {

    private final DiscordUtils discordUtils;
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final ScoreRepository scoreRepository;
    private final BeatLeaderUtils beatLeaderUtils;
    private final BeatSaverUtils beatSaverUtils;

    @Override
    @Transactional
    public Boolean updateSongScoreFromBeatLeader(Long songSeq) throws Exception {

        Song songEntity = songRepository.findBySeq(songSeq)
                .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 곡을 찾을 수 없습니다."));

        /**
         * 유저 정보를 가져온 후, DTO 에 삽입한다.
         */
        List<User> users = userRepository.findAll();
        for (User user : users) {
            beatLeaderUtils.saveScoreFromBeatLeaderAPI(user, songEntity);
        }

        return true;

    }

    @Override
    @Transactional
    public Page<ScoreResponseDto> findSongScore(Long songSeq, Integer page, String sort, Integer elementCnt) throws Exception {
        Song songEntity = songRepository.findBySeq(songSeq)
                .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 곡을 찾을 수 없습니다."));

        // 페이징 객체를 생성한다.
        Pageable pageable = PageRequest.of(page - 1, elementCnt == null ? 10 : elementCnt);

        return scoreRepository.findAllScoreBySongSeqWithPage(songSeq, pageable, sort)
                .map(score -> ScoreResponseDto.builder().entity(score).build());
    }

    @Override
    @Transactional
    public ScoreResponseDto saveScore(ScoreSaveRequestDto requestDto, String remoteAddr) throws Exception {
        PrincipalUserDetail userDetails = (PrincipalUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User userEntity = userRepository.findBySeq(userDetails.getUserSeq())
                .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 유저를 찾을 수 없습니다."));

        String songHash = requestDto.getSongHash().toLowerCase();

        Song songEntity = songRepository.findBySongModeTypeAndSongHashAndSongDifficulty(requestDto.getSongModeType(), songHash, requestDto.getSongDifficulty());

        if (songEntity == null) {
            beatSaverUtils.saveSongByHashFromBeatSaverAPI(songHash);
            // 노래 재 조회
            songEntity = songRepository.findBySongModeTypeAndSongHashAndSongDifficulty(requestDto.getSongModeType(), songHash, requestDto.getSongDifficulty());

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
                .timePost(LocalDateTime.now())
                .regIp(remoteAddr)
                .build();

        scoreRepository.save(score);

        // Discord Embed 전송용 데이터 초기화
        Author author = Author.builder()
                .name(userEntity.getNickName())
                .iconUrl(userEntity.getImageUrl())
                .build();

        List<DiscordEmbed> discordEmbed = new ArrayList<>();
        discordEmbed.add(
                DiscordEmbed.builder()
                        .author(author)
                        .title("신규 스코어가 등록되었습니다!")
                        .description(songEntity.getSongName() + " - " + songEntity.getSongModeType() + " - " + songEntity.getSongModeType() +"\n" +
                                score.getAccuracy() + "% " +score.getModifiedScore() + "점 " + score.getBadCut() + score.getBombCut() + score.getWallsHit() + score.getMissedNote() + ":x:")
                        .color(0xFF0000)
                        .build()
        );

        DiscordMessage discordMessage = DiscordMessage.builder()
                .embeds(discordEmbed)
                .build();

        discordUtils.sendEmbedMessage(discordMessage);

        return ScoreResponseDto.builder().entity(score).build();
    }

    @Override
    @Transactional
    public ScoreResponseDto updateScoreFromBeatLeader(Long userSeq) throws Exception {
        /**
         * 유저 정보를 가져온 후, DTO 에 삽입한다.
         */
        User userEntity = userRepository.findBySeq(userSeq)
                .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 유저를 찾을 수 없습니다. userSeq = " + userSeq));

        beatLeaderUtils.saveScoreByUserFromBeatLeaderAPI(userEntity);

        return null;
    }

    @Override
    public Page<ScoreResponseDto> findUserScore(Long userSeq, Integer page, String sort, Integer elementCnt) throws Exception {
        User userEntity = userRepository.findBySeq(userSeq)
                .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 유저를 찾을 수 없습니다. userSeq = " + userSeq));

        // 페이징 객체를 생성한다.
        Pageable pageable = PageRequest.of(page - 1, elementCnt == null ? 10 : elementCnt);

        return scoreRepository.findAllScoreByUserWithPage(userEntity, pageable, sort)
                .map(score -> ScoreResponseDto.builder().entity(score).build());
    }
}
