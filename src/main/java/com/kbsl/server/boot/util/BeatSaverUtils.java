package com.kbsl.server.boot.util;

import com.kbsl.server.boot.exception.RestException;
import com.kbsl.server.song.domain.model.Song;
import com.kbsl.server.song.domain.repository.SongRepository;
import com.kbsl.server.song.dto.response.SongApiResponseDto;
import com.kbsl.server.song.enums.SongDifficultyType;
import com.kbsl.server.song.enums.SongModeType;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.JSONValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BeatSaverUtils {
    private String beatSaverUrl = "https://api.beatsaver.com";
    private final SongRepository songRepository;

    public List<SongApiResponseDto> saveSongByHashFromBeatSaverAPI(String songHash){
        List<SongApiResponseDto> songApiResponseDtoArrayList = new ArrayList<>();

        try {
            URI uri = UriComponentsBuilder
                    .fromUriString(beatSaverUrl)
                    .pathSegment("maps", "hash", songHash.toLowerCase())
                    .encode()
                    .build()
                    .toUri();

            log.info("Request URI: " + uri);

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(uri, String.class);

            /**
             * BeatSaver 데이터가 존재하지 않을경우 패스한다.
             */
            JSONObject responseJson = (JSONObject) JSONValue.parse(response);
            if (responseJson == null) {
                throw new RestException(HttpStatus.BAD_REQUEST, "잘못된 JSON 응답입니다. BeatLeader API: " + response);
            }
//        log.info(response);

            JSONObject responseUploaderJson = (JSONObject) JSONValue.parse(responseJson.get("uploader").toString());
            JSONArray responseVersionsJson = (JSONArray) JSONValue.parse(responseJson.get("versions").toString());

            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSz");
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSz");

            // 플레이 한 노래 저장
            for (Object responseVersionObject : responseVersionsJson){
                JSONObject responseVersionsJsonObject = (JSONObject) JSONValue.parse(responseVersionObject.toString());
                JSONArray responseDiffsJson = (JSONArray) JSONValue.parse(responseVersionsJsonObject.get("diffs").toString());

                for (Object responseDiffObject : responseDiffsJson) {
                    JSONObject responseDiffsJsonObject = (JSONObject) JSONValue.parse(responseDiffObject.toString());

                    if (songRepository.findBySongModeTypeAndSongHashAndSongDifficulty(SongModeType.valueOf(responseDiffsJsonObject.get("characteristic").toString()), responseVersionsJsonObject.get("hash").toString(), SongDifficultyType.valueOf(responseDiffsJsonObject.get("difficulty").toString())) != null){
                        log.error("이미 등록된 노래입니다. 패스합니다.");
                        continue;
                    }

                    LocalDateTime publishedDtime;

                    try{
                        publishedDtime = LocalDateTime.parse(responseVersionsJsonObject.get("createdAt").toString(), formatter1);
                    }catch (Exception e){
                        publishedDtime = LocalDateTime.parse(responseVersionsJsonObject.get("createdAt").toString(), formatter2);
                    }

                    Song songEntity = Song.builder()
                            .songId(responseJson.get("id").toString())
                            .songHash(responseVersionsJsonObject.get("hash").toString())
                            .songName(responseJson.get("name").toString())
                            .songDifficulty(SongDifficultyType.valueOf(responseDiffsJsonObject.get("difficulty").toString()))
                            .songModeType(SongModeType.valueOf(responseDiffsJsonObject.get("characteristic").toString()))
                            .uploaderName(responseUploaderJson.get("name").toString())
                            .coverUrl(responseVersionsJsonObject.get("coverURL").toString())
                            .previewUrl(responseVersionsJsonObject.get("previewURL").toString())
                            .downloadUrl(responseVersionsJsonObject.get("downloadURL").toString())
                            .publishedDtime(publishedDtime)
                            .build();

                    songRepository.save(songEntity);

                    songApiResponseDtoArrayList.add(SongApiResponseDto.builder().entity(songEntity).build());
                }
            }
            return songApiResponseDtoArrayList;
        }catch (Exception e){
            log.error("노래를 찾을 수 없습니다. BeatSaver API 오류" + e.getMessage());
        }

        return songApiResponseDtoArrayList;
    }

    public List<SongApiResponseDto> saveSongByIdFromBeatSaverAPI(String id){

        List<SongApiResponseDto> songApiResponseDtoArrayList = new ArrayList<>();

        URI uri = UriComponentsBuilder
            .fromUriString(beatSaverUrl)
            .pathSegment("maps", "id", id.toLowerCase())
            .encode()
            .build()
            .toUri();

        log.info("Request URI: " + uri);

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(uri, String.class);

        /**
         * BeatSaver 데이터가 존재하지 않을경우 패스한다.
         */
        JSONObject responseJson = (JSONObject) JSONValue.parse(response);
        if (responseJson == null) {
            throw new RestException(HttpStatus.BAD_REQUEST, "잘못된 JSON 응답입니다. BeatLeader API: " + response);
        }
//        log.info(response);

        JSONObject responseUploaderJson = (JSONObject) JSONValue.parse(responseJson.get("uploader").toString());
        JSONArray responseVersionsJson = (JSONArray) JSONValue.parse(responseJson.get("versions").toString());

        for (Object responseVersionObject : responseVersionsJson) {
            JSONObject responseVersionsJsonObject = (JSONObject) JSONValue.parse(responseVersionObject.toString());
            JSONArray responseDiffsJson = (JSONArray) JSONValue.parse(responseVersionsJsonObject.get("diffs").toString());

            for (Object responseDiffObject : responseDiffsJson) {
                JSONObject responseDiffsJsonObject = (JSONObject) JSONValue.parse(responseDiffObject.toString());

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSz");

                if (songRepository.findBySongModeTypeAndSongHashAndSongDifficulty(SongModeType.valueOf(responseDiffsJsonObject.get("characteristic").toString()), responseVersionsJsonObject.get("hash").toString(), SongDifficultyType.valueOf(responseDiffsJsonObject.get("difficulty").toString())) != null){
                    log.error("이미 등록된 노래입니다. 패스합니다.");
                    continue;
                }

                Song songEntity = Song.builder()
                    .songId(responseJson.get("id").toString())
                    .songHash(responseVersionsJsonObject.get("hash").toString())
                    .songName(responseJson.get("name").toString())
                    .songDifficulty(SongDifficultyType.valueOf(responseDiffsJsonObject.get("difficulty").toString()))
                    .songModeType(SongModeType.valueOf(responseDiffsJsonObject.get("characteristic").toString()))
                    .uploaderName(responseUploaderJson.get("name").toString())
                    .coverUrl(responseVersionsJsonObject.get("coverURL").toString())
                    .previewUrl(responseVersionsJsonObject.get("previewURL").toString())
                    .downloadUrl(responseVersionsJsonObject.get("downloadURL").toString())
                    .publishedDtime(LocalDateTime.parse(responseVersionsJsonObject.get("createdAt").toString(), formatter))
                    .build();

                songRepository.save(songEntity);

                songApiResponseDtoArrayList.add(SongApiResponseDto.builder().entity(songEntity).build());
            }
        }
        return songApiResponseDtoArrayList;
    }

}
