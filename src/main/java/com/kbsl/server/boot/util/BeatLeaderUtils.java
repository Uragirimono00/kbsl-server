package com.kbsl.server.boot.util;

import com.kbsl.server.boot.exception.RestException;
import com.kbsl.server.score.domain.model.Score;
import com.kbsl.server.score.domain.repository.ScoreRepository;
import com.kbsl.server.song.domain.model.Song;
import com.kbsl.server.song.domain.repository.SongRepository;
import com.kbsl.server.song.dto.response.SongApiResponseDto;
import com.kbsl.server.song.enums.SongDifficultyType;
import com.kbsl.server.song.enums.SongModeType;
import com.kbsl.server.user.domain.model.User;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.JSONValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BeatLeaderUtils {

    private String beatLeaderUrl = "https://api.beatleader.xyz";

    private final ScoreRepository scoreRepository;
    private final SongRepository songRepository;
    private final BeatSaverUtils beatSaverUtils;

    public void saveScoreFromBeatLeaderAPI(User user, Song songEntity) {
        try {
            URI uri = UriComponentsBuilder
                    .fromUriString(beatLeaderUrl)
                    .pathSegment("score", user.getSteamId(), songEntity.getSongHash(), songEntity.getSongDifficulty().toString(), songEntity.getSongModeType().toString())
                    .encode()
                    .build()
                    .toUri();

            log.info("Request URI: " + uri);

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(uri, String.class);

            /**
             * BeatLeader 데이터가 없는 경우 통과
             */
            JSONObject responseJson = (JSONObject) JSONValue.parse(response);
            if (responseJson == null) {
                log.error("Invalid JSON response. BeatLeader API: " + response);
                return;
            }
            log.info(responseJson.get("timepost").toString());

            /**
             * 이미 등록된 점수의 경우 통과
             */
            long scoreSeq = Long.parseLong(responseJson.get("id").toString());
            if (scoreRepository.existsByScoreSeq(scoreSeq)) {
                log.info("이미 등록된 점수입니다. scoreSeq = " + scoreSeq);
                return;
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
                    // 미국 시간에서 한국 시간으로 변환하기 위해 9시간 추가
                    .timePost(LocalDateTime.ofEpochSecond(Long.parseLong(responseJson.get("timepost").toString()), 0, ZoneOffset.of("+09:00")))
                    .build();

            scoreRepository.save(score);

        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    public void saveScoreByUserFromBeatLeaderAPI(User user) {
        
        // todo : 전체리스트 가져와서 전부다 넣도록 수정 해야해
        
        URI uri = UriComponentsBuilder
                .fromUriString(beatLeaderUrl)
                .pathSegment("player", user.getSteamId(), "scores")
                .queryParam("page", 1)
                .queryParam("count", 30)
                .encode()
                .build()
                .toUri();

        log.info("Request URI: " + uri);

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(uri, String.class);

        /**
         * BeatLeader 데이터가 없는 경우 통과
         */
        JSONObject responseJson = (JSONObject) JSONValue.parse(response);
        if (responseJson == null) {
            log.error("Invalid JSON response. BeatLeader API: " + response);
            return;
        }

        JSONArray responseDataJson = (JSONArray) JSONValue.parse(responseJson.get("data").toString());
//        log.info(responseDataJson.toString());

        // 플레이 한 노래 저장
        for (Object responseDataObj : responseDataJson) {
            JSONObject data = (JSONObject) JSONValue.parse(responseDataObj.toString());
            JSONObject leaderboard = (JSONObject) JSONValue.parse(data.get("leaderboard").toString());
            JSONObject songInfo = (JSONObject) JSONValue.parse(leaderboard.get("song").toString());
            JSONObject playSongInfo = (JSONObject) JSONValue.parse(leaderboard.get("difficulty").toString());
            log.info(playSongInfo.get("difficultyName").toString());
            log.info(playSongInfo.get("modeName").toString());

            /**
             * 이미 등록된 점수의 경우 통과
             */
            long scoreSeq = Long.parseLong(data.get("id").toString());
            if (scoreRepository.existsByScoreSeq(scoreSeq)) {
                log.info("이미 등록된 점수입니다. scoreSeq = " + scoreSeq);
                continue;
            }

            Song songEntity = songRepository.findBySongModeTypeAndSongHashAndSongDifficulty(SongModeType.valueOf(playSongInfo.get("modeName").toString()), songInfo.get("hash").toString().toLowerCase(), SongDifficultyType.valueOf(playSongInfo.get("difficultyName").toString()));


            if (songEntity == null){
                beatSaverUtils.saveSongByHashFromBeatSaverAPI(songInfo.get("hash").toString());
            }

            songEntity = songRepository.findBySongModeTypeAndSongHashAndSongDifficulty(SongModeType.valueOf(playSongInfo.get("modeName").toString()), songInfo.get("hash").toString().toLowerCase(), SongDifficultyType.valueOf(playSongInfo.get("difficultyName").toString()));

            if (songEntity == null) {
                log.info(playSongInfo.get("modeName").toString() + " / " + songInfo.get("hash").toString().toLowerCase() + " / " + playSongInfo.get("difficultyName").toString());
                log.info("노래를 찾을 수 없습니다. 다음으로 넘어갑니다.");
                continue;
            }
            Score score = Score.builder()
                    .user(user)
                    .song(songEntity)
                    .scoreSeq(scoreSeq)
                    .baseScore(Long.parseLong(data.get("baseScore").toString()))
                    .modifiedScore(Long.parseLong(data.get("modifiedScore").toString()))
                    .accuracy(Double.parseDouble(data.get("accuracy").toString()))
                    .badCut(Integer.parseInt(data.get("badCuts").toString()))
                    .missedNote(Integer.parseInt(data.get("badCuts").toString()))
                    .bombCut(Integer.parseInt(data.get("badCuts").toString()))
                    .wallsHit(Integer.parseInt(data.get("wallsHit").toString()))
                    .pause(Integer.parseInt(data.get("pauses").toString()))
                    .playCount(Integer.parseInt(data.get("playCount").toString()))
                    .accLeft(Double.parseDouble(data.get("accLeft").toString()))
                    .accRight(Double.parseDouble(data.get("accRight").toString()))
                    .comment("")
                    // 미국 시간에서 한국 시간으로 변환하기 위해 9시간 추가
                    .timePost(LocalDateTime.ofEpochSecond(Long.parseLong(data.get("timepost").toString()), 0, ZoneOffset.of("+09:00")))
                    .build();

            scoreRepository.save(score);
        }
    }
}