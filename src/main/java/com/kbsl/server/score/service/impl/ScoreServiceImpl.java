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
import com.kbsl.server.song.dto.response.SongApiResponseDto;
import com.kbsl.server.song.enums.SongDifficultyType;
import com.kbsl.server.song.enums.SongModeType;
import com.kbsl.server.user.domain.model.User;
import com.kbsl.server.user.domain.repository.UserRepository;
import com.nimbusds.jose.shaded.json.JSONArray;
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
import java.util.ArrayList;
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
    public ScoreResponseDto saveScoreWithSteamId(ScoreSaveRequestDto requestDto) throws Exception {

        User userEntity = userRepository.findBySteamId(requestDto.getSteamId())
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 유저를 찾을 수 없습니다. steamId = " + requestDto.getSteamId()));

        Song songEntity = songRepository.findBySongModeTypeAndSongHashAndSongDifficulty(requestDto.getSongModeType(), requestDto.getSongHash(), requestDto.getSongDifficulty());
        if (songEntity == null){
            List<SongApiResponseDto> songApiResponseDtoArrayList = new ArrayList<>();

            URI uri = UriComponentsBuilder
                .fromUriString("https://api.beatsaver.com")
                .pathSegment("maps", "hash", requestDto.getSongHash())
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
                throw new RestException(HttpStatus.BAD_REQUEST, "잘못된 JSON 응답입니다. BeatLeader API: " + response);
            }
//        log.info(response);

            JSONObject responseUploaderJson = (JSONObject) JSONValue.parse(responseJson.get("uploader").toString());
            JSONArray responseVersionsJson = (JSONArray) JSONValue.parse(responseJson.get("versions").toString());


            // 플레이 한 노래 저장
            for (Object responseVersionObject : responseVersionsJson){
                JSONObject responseVersionsJsonObject = (JSONObject) JSONValue.parse(responseVersionObject.toString());
                JSONArray responseDiffsJson = (JSONArray) JSONValue.parse(responseVersionsJsonObject.get("diffs").toString());

                for (Object responseDiffObject : responseDiffsJson) {
                    JSONObject responseDiffsJsonObject = (JSONObject) JSONValue.parse(responseDiffObject.toString());

                    songEntity = Song.builder()
                        .songId(responseJson.get("id").toString())
                        .songHash(responseVersionsJsonObject.get("hash").toString())
                        .songName(responseJson.get("name").toString())
                        .songDifficulty(SongDifficultyType.valueOf(responseDiffsJsonObject.get("difficulty").toString()))
                        .songModeType(SongModeType.valueOf(responseDiffsJsonObject.get("characteristic").toString()))
                        .uploaderName(responseUploaderJson.get("name").toString())
                        .coverUrl(responseVersionsJsonObject.get("coverURL").toString())
                        .previewUrl(responseVersionsJsonObject.get("previewURL").toString())
                        .downloadUrl(responseVersionsJsonObject.get("downloadURL").toString())
                        .build();

                    songRepository.save(songEntity);

                    songApiResponseDtoArrayList.add(SongApiResponseDto.builder().entity(songEntity).build());
                }
            }
        }
        // 노래 재 조회
        songEntity = songRepository.findBySongModeTypeAndSongHashAndSongDifficulty(requestDto.getSongModeType(), requestDto.getSongHash(), requestDto.getSongDifficulty());
        if (songEntity == null) {
            // todo: 스코어 기록이 실패할 경우 log에 해당기록을 남겨 스코어를 잃어버리지 않도록 예외를 처리한다.
            throw new RestException(HttpStatus.NOT_FOUND, "노래를 찾을 수 없습니다... 재시도 해주십시오...");
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
