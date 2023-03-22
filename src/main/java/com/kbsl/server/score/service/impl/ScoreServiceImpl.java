package com.kbsl.server.score.service.impl;

import com.kbsl.server.auth.domain.repository.AuthTokenRepository;
import com.kbsl.server.boot.exception.RestException;
import com.kbsl.server.league.domain.model.League;
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
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.JSONValue;
import com.nimbusds.jose.shaded.json.parser.JSONParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.TimeZone;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {

    private final UserRepository userRepository;
    private final LeagueRepository leagueRepository;
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
            try{
                URI uri = UriComponentsBuilder
                        .fromUriString("https://api.beatleader.xyz")
                        .pathSegment("score", user.getSteamId(), songEntity.getSongHash(), songEntity.getSongDifficulty().toString(), songEntity.getSongModeType().toString())
                        .encode()
                        .build()
                        .toUri();

                log.info("Request URI: " + uri);

                RestTemplate restTemplate = new RestTemplate();
                String response = restTemplate.getForObject(uri, String.class);

                /**
                 * BeatLeader 데이터가 존재하지 않을경우 패스한다.
                 */
                JSONObject responseJson = (JSONObject) JSONValue.parse(response);
                if (responseJson == null) {
                    log.error("잘못된 JSON 응답입니다. BeatLeader API: " + response);
                    continue;
                }
                log.info(responseJson.get("timepost").toString());

                /**
                 * 이미 등록된 점수의 경우 패스한다.
                 */
                long scoreSeq = Long.parseLong(responseJson.get("id").toString());
                if (scoreRepository.existsByScoreSeq(scoreSeq)) {
                    log.info("이미 등록된 스코어 입니다. scoreSeq = " + scoreSeq);
                    continue;
                }

                Score score = Score.builder()
                        .user(user)
                        .song(songEntity)
                        .scoreSeq(scoreSeq)
                        .baseScore(Long.parseLong(responseJson.get("baseScore").toString()))
                        .modifiedScore(Long.parseLong(responseJson.get("modifiedScore").toString()))
                        .accuracy(Double.parseDouble(responseJson.get("accuracy").toString()))
                        .badCut(Integer.parseInt(responseJson.get("badCuts").toString()))
                        .missedNote(Integer.parseInt(responseJson.get("badCuts").toString()))
                        .bombCut(Integer.parseInt(responseJson.get("badCuts").toString()))
                        .wallsHit(Integer.parseInt(responseJson.get("wallsHit").toString()))
                        .pause(Integer.parseInt(responseJson.get("pauses").toString()))
                        .playCount(Integer.parseInt(responseJson.get("playCount").toString()))
                        .accLeft(Double.parseDouble(responseJson.get("accLeft").toString()))
                        .accRight(Double.parseDouble(responseJson.get("accRight").toString()))
                        .comment("")
                        // timePost의 경우 한국시간으로 변환하기 위해 미국시간에서 9시간을 더한다.
                        .timePost(LocalDateTime.ofEpochSecond(Long.parseLong(responseJson.get("timepost").toString()), 0, ZoneOffset.of("+09:00")))
                        .build();

                scoreRepository.save(score);

            }catch (Exception e){
                log.error(e.toString());
                continue;
            }

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
    public ScoreResponseDto saveScoreWithSteamId(ScoreSaveRequestDto scoreSaveRequestDto) throws Exception {
        User userEntity = userRepository.findBySteamId(scoreSaveRequestDto.getSteamId())
                .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 유저를 찾을 수 없습니다. steamId = " + scoreSaveRequestDto.getSteamId()));

        Song songEntity = songRepository.findBySongModeTypeAndSongHashAndSongDifficulty(scoreSaveRequestDto.getSongSaveRequestDto().getSongModeType(), scoreSaveRequestDto.getSongSaveRequestDto().getSongHash(), scoreSaveRequestDto.getSongSaveRequestDto().getSongDifficulty());
        // 존재하는 노래가 없을경우 노래 데이터 생성
        if (songEntity == null) {
            songEntity = Song.builder()
                    .songId(scoreSaveRequestDto.getSongSaveRequestDto().getSongId())
                    .songName(scoreSaveRequestDto.getSongSaveRequestDto().getSongName())
                    .songHash(scoreSaveRequestDto.getSongSaveRequestDto().getSongHash())
                    .songDifficulty(scoreSaveRequestDto.getSongSaveRequestDto().getSongDifficulty())
                    .songModeType(scoreSaveRequestDto.getSongSaveRequestDto().getSongModeType())
                    .downloadUrl(scoreSaveRequestDto.getSongSaveRequestDto().getDownloadUrl())
                    .previewUrl(scoreSaveRequestDto.getSongSaveRequestDto().getPreviewUrl())
                    .coverUrl(scoreSaveRequestDto.getSongSaveRequestDto().getCoverUrl())
                    .uploaderName(scoreSaveRequestDto.getSongSaveRequestDto().getUploaderName())
                    .build();

            songRepository.save(songEntity);
        }

        Score score = Score.builder()
                .user(userEntity)
                .song(songEntity)
                .scoreSeq(0L) // 비트리더 기록이 아니기에 0L로 취급한다.
                .baseScore(scoreSaveRequestDto.getBaseScore())
                .modifiedScore(scoreSaveRequestDto.getModifiedScore())
                .accuracy(scoreSaveRequestDto.getAccuracy())
                .badCut(scoreSaveRequestDto.getBadCut())
                .missedNote(scoreSaveRequestDto.getMissedNote())
                .bombCut(scoreSaveRequestDto.getBombCut())
                .wallsHit(scoreSaveRequestDto.getWallsHit())
                .pause(scoreSaveRequestDto.getPause())
                .playCount(scoreSaveRequestDto.getPlayCount())
                .accLeft(scoreSaveRequestDto.getAccLeft())
                .accRight(scoreSaveRequestDto.getAccRight())
                .comment("")
                // timePost의 경우 한국시간으로 변환하기 위해 미국시간에서 9시간을 더한다.
                .timePost(scoreSaveRequestDto.getTimePost())
                .build();

        scoreRepository.save(score);

        return ScoreResponseDto.builder().entity(score).build();
    }

}
