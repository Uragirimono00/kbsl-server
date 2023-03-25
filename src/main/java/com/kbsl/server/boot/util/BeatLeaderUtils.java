package com.kbsl.server.boot.util;

import com.kbsl.server.score.domain.model.Score;
import com.kbsl.server.score.domain.repository.ScoreRepository;
import com.kbsl.server.song.domain.model.Song;
import com.kbsl.server.song.domain.repository.SongRepository;
import com.kbsl.server.user.domain.model.User;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.JSONValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@RequiredArgsConstructor
public class BeatLeaderUtils {

    public static void saveScoreFromBeatLeaderAPI(User user, Song songEntity, ScoreRepository scoreRepository) {
        try {
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
}
