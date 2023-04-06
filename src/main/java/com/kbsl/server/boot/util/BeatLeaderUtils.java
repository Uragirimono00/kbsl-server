package com.kbsl.server.boot.util;

import com.kbsl.server.score.domain.model.Score;
import com.kbsl.server.score.domain.repository.ScoreRepository;
import com.kbsl.server.song.domain.model.Song;
import com.kbsl.server.song.domain.repository.SongRepository;
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

@Component
@Slf4j
@RequiredArgsConstructor
public class BeatLeaderUtils {

    private String beatLeaderUrl = "https://api.beatleader.xyz";
    private Integer count = 100;

    private final ScoreRepository scoreRepository;
    private final SongRepository songRepository;
    private final BeatSaverUtils beatSaverUtils;

    /**
     * 특정 곡의 대한 모든 유저의 점수 기록을 Beatleader API 를 통해 가져온다.
     * @param user
     * @param songEntity
     */
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

            /**
             * 점수 저장
             */
            saveScore(user, songEntity, responseJson);

        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    /**
     * 특정 유저의 스코어를 Beatleader를 통해 가져온다.
     * 단, Steam Id가 조회되지 않을 경우 예외를 발생시킨다.
     * @param user
     */
    public void saveScoreByUserFromBeatLeaderAPI(User user) {

//        if (user.getSteamId() != null){
//            log.error("steamId가 존재하지 않는 유저입니다.");
//            return;
//        }

        URI compactUri = UriComponentsBuilder
            .fromUriString(beatLeaderUrl)
            .pathSegment("player", user.getUsername(), "scores", "compact")
            .queryParam("page", 1)
            .queryParam("count", count)
            .encode()
            .build()
            .toUri();

        log.info("Request URI: " + compactUri);

        RestTemplate compactRestTemplate = new RestTemplate();
        String compactResponse = compactRestTemplate.getForObject(compactUri, String.class);

        /**
         * BeatLeader 데이터가 없는 경우 통과
         */
        JSONObject compactResponseJson = (JSONObject) JSONValue.parse(compactResponse);
        if (compactResponseJson == null) {
            log.error("Invalid JSON response. BeatLeader API: " + compactResponse);
            return;
        }

        JSONArray compactResponseDataJson = (JSONArray) JSONValue.parse(compactResponseJson.get("data").toString());
        JSONObject compactResponseMetaJson = (JSONObject) JSONValue.parse(compactResponseJson.get("metadata").toString());
//        log.info(responseDataJson.toString());

        Integer totalPage = Integer.parseInt(compactResponseMetaJson.get("total").toString()) / count  + 1;

        log.info("totalPage = " + totalPage);

        for (Integer page = 1; page <= totalPage; page++) {

            log.error(page.toString());

            try {
                URI uri = UriComponentsBuilder
                    .fromUriString(beatLeaderUrl)
                    .pathSegment("player", user.getSteamId(), "scores")
                    .queryParam("page", page)
                    .queryParam("count", count)
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
                JSONObject responseMetaJson = (JSONObject) JSONValue.parse(responseJson.get("metadata").toString());


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

                    /**
                     * DB에 없는 노래의 경우 beatSaver API 를 호출해 새로 저장
                     */
                    if (songEntity == null) {
                        beatSaverUtils.saveSongByHashFromBeatSaverAPI(songInfo.get("hash").toString());
                    }

                    songEntity = songRepository.findBySongModeTypeAndSongHashAndSongDifficulty(SongModeType.valueOf(playSongInfo.get("modeName").toString()), songInfo.get("hash").toString().toLowerCase(), SongDifficultyType.valueOf(playSongInfo.get("difficultyName").toString()));

                    if (songEntity == null) {
                        log.info(playSongInfo.get("modeName").toString() + " / " + songInfo.get("hash").toString().toLowerCase() + " / " + playSongInfo.get("difficultyName").toString());
                        log.info("노래를 찾을 수 없습니다. 다음으로 넘어갑니다.");
                        continue;
                    }

                    /**
                     * 점수 저장
                     */
                    saveScore(user, songEntity, data);


                }
            } catch (Exception e) {
                log.info(e.getMessage());
            }
        }

    }

    /**
     * 점수를 저장한다.
     * @param user
     * @param songEntity
     * @param scoreData
     */
    private void saveScore(User user, Song songEntity, JSONObject scoreData) {
        Score score = Score.builder()
            .user(user)
            .song(songEntity)
            .scoreSeq(Long.parseLong(scoreData.get("id").toString()))
            .baseScore(Long.parseLong(scoreData.get("baseScore").toString()))
            .modifiedScore(Long.parseLong(scoreData.get("modifiedScore").toString()))
            .accuracy(Double.parseDouble(scoreData.get("accuracy").toString()))
            .badCut(Integer.parseInt(scoreData.get("badCuts").toString()))
            .missedNote(Integer.parseInt(scoreData.get("badCuts").toString()))
            .bombCut(Integer.parseInt(scoreData.get("badCuts").toString()))
            .wallsHit(Integer.parseInt(scoreData.get("wallsHit").toString()))
            .pause(Integer.parseInt(scoreData.get("pauses").toString()))
            .playCount(Integer.parseInt(scoreData.get("playCount").toString()))
            .accLeft(Double.parseDouble(scoreData.get("accLeft").toString()))
            .accRight(Double.parseDouble(scoreData.get("accRight").toString()))
            .comment("")
            // 미국 시간에서 한국 시간으로 변환하기 위해 9시간 추가
            .timePost(LocalDateTime.ofEpochSecond(Long.parseLong(scoreData.get("timepost").toString()), 0, ZoneOffset.of("+09:00")))
            .build();

        scoreRepository.save(score);
    }
}