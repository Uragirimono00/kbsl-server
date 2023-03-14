package com.kbsl.server.score.service.impl;

import com.kbsl.server.boot.exception.RestException;
import com.kbsl.server.league.dto.response.LeagueResponseDto;
import com.kbsl.server.score.domain.model.Score;
import com.kbsl.server.score.domain.repository.ScoreRepository;
import com.kbsl.server.score.dto.response.ScoreResponseDto;
import com.kbsl.server.score.service.ScoreService;
import com.kbsl.server.song.domain.model.Song;
import com.kbsl.server.song.domain.model.SongRepository;
import com.kbsl.server.user.domain.model.User;
import com.kbsl.server.user.domain.repository.UserRepository;
import com.kbsl.server.user.dto.response.UserResponseDto;
import com.kbsl.server.user.service.principal.PrincipalUserDetail;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.parser.JSONParser;
import io.swagger.models.auth.In;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
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
    public Page<ScoreResponseDto> updatePlayerScore(Long songSeq, Integer page, String sort, Integer elementCnt) throws Exception {


        Song songEntity = songRepository.findBySeq(songSeq)
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 곡을 찾을 수 없습니다."));

        // 데이터가 아예 없을때 비트리더 조회해서 데이터 넣기
//        try {

        /**
         * 유저 정보를 가져온 후, DTO 에 삽입한다.
         */
        List<User> userEntityList = userRepository.findALL();
        for (User userEntity : userEntityList) {
            if (userEntity.getBeatleaderId() != null) {
                continue;
            }

            URI uri = UriComponentsBuilder
                .fromUriString("https://api.beatleader.xyz")
                .pathSegment("score", userEntity.getBeatleaderId(), songEntity.getSongHash(), songEntity.getSongDifficulty().toString(), songEntity.getSongModeType().toString())
                .encode()
                .build()
                .toUri();

            log.info(uri.toString());


            RestTemplate restTemplete = new RestTemplate();

            String result = restTemplete.getForObject(uri, String.class);

            JSONParser jsonParser = new JSONParser();
            Object obj = jsonParser.parse(result);
            JSONObject resultJsonObj = (JSONObject) obj;

            System.out.println(resultJsonObj);

            if (scoreRepository.findByScoreSeq(Long.parseLong(resultJsonObj.get("id").toString())).isPresent())
                throw new RestException(HttpStatus.BAD_REQUEST, "이미 등록된 스코어 입니다. scoreSeq = " + Long.parseLong(resultJsonObj.get("id").toString()));

            Score scoreSaveEntity = Score.builder()
                .user(userEntity)
                .song(songEntity)
                .scoreSeq(Long.parseLong(resultJsonObj.get("id").toString()))
                .baseScore(Long.parseLong(resultJsonObj.get("baseScore").toString()))
                .modifiedScore(Long.parseLong(resultJsonObj.get("modifiedScore").toString()))
                .accuracy(Double.parseDouble(resultJsonObj.get("accuracy").toString()))
                .badCut(Integer.parseInt(resultJsonObj.get("badCuts").toString()))
                .missedNote(Integer.parseInt(resultJsonObj.get("badCuts").toString()))
                .bombCut(Integer.parseInt(resultJsonObj.get("badCuts").toString()))
                .wallsHit(Integer.parseInt(resultJsonObj.get("wallsHit").toString()))
                .pause(Integer.parseInt(resultJsonObj.get("pauses").toString()))
                .playCount(Integer.parseInt(resultJsonObj.get("playCount").toString()))
                .accLeft(Double.parseDouble(resultJsonObj.get("accLeft").toString()))
                .accRight(Double.parseDouble(resultJsonObj.get("accRight").toString()))
                .comment("")
                .timePost(Integer.parseInt(resultJsonObj.get("timepost").toString()))
                .build();

            scoreRepository.save(scoreSaveEntity);

        }

        // 페이징 객체를 생성한다.
//        Pageable pageable = PageRequest.of(page, elementCnt == null ? 10 : elementCnt);

        return null;

//        return scoreRepository.findAllScoreBySongSeqWithPage(songSeq, pageable, sort)
//            .map(score -> ScoreResponseDto.builder().entity(score).build());

        //에러처리해야댐
//        } catch (HttpClientErrorException | HttpServerErrorException e) {
//            log.info(e.toString());
//            throw new RestException(HttpStatus.BAD_REQUEST, "존재하지 않는 Beatleader ID 입니다.");
//        } catch (Exception e) {
//            log.info(e.toString());
//            return null;
//        }

    }
}
