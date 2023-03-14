package com.kbsl.server.score.service.impl;

import com.kbsl.server.boot.exception.RestException;
import com.kbsl.server.score.domain.model.Score;
import com.kbsl.server.score.domain.repository.ScoreRepository;
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
import java.util.List;

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
        List<User> users = userRepository.findByBeatleaderIdIsNotNull();
        for (User user : users) {
            String beatleaderId = user.getBeatleaderId();
            URI uri = UriComponentsBuilder
                    .fromUriString("https://api.beatleader.xyz")
                    .pathSegment("score", beatleaderId, songEntity.getSongHash(), songEntity.getSongDifficulty().toString(), songEntity.getSongModeType().toString())
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
                    .timePost(Integer.parseInt(responseJson.get("timepost").toString()))
                    .build();

            scoreRepository.save(score);
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
}
