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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class BeatSaverUtils {
    public static List<SongApiResponseDto> saveSongByHashFromBeatSaverAPI(String songHash, SongRepository songRepository){
        List<SongApiResponseDto> songApiResponseDtoArrayList = new ArrayList<>();

        URI uri = UriComponentsBuilder
            .fromUriString("https://api.beatsaver.com")
            .pathSegment("maps", "hash", songHash.toLowerCase())
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSz");

        // 플레이 한 노래 저장
        for (Object responseVersionObject : responseVersionsJson){
            JSONObject responseVersionsJsonObject = (JSONObject) JSONValue.parse(responseVersionObject.toString());
            JSONArray responseDiffsJson = (JSONArray) JSONValue.parse(responseVersionsJsonObject.get("diffs").toString());

            for (Object responseDiffObject : responseDiffsJson) {
                JSONObject responseDiffsJsonObject = (JSONObject) JSONValue.parse(responseDiffObject.toString());

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

    public static List<SongApiResponseDto> saveSongByIdFromBeatSaverAPI(String id, SongRepository songRepository){

        List<SongApiResponseDto> songApiResponseDtoArrayList = new ArrayList<>();

        URI uri = UriComponentsBuilder
            .fromUriString("https://api.beatsaver.com")
            .pathSegment("maps", "id", id.toLowerCase())
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

        for (Object responseVersionObject : responseVersionsJson) {
            JSONObject responseVersionsJsonObject = (JSONObject) JSONValue.parse(responseVersionObject.toString());
            JSONArray responseDiffsJson = (JSONArray) JSONValue.parse(responseVersionsJsonObject.get("diffs").toString());

            for (Object responseDiffObject : responseDiffsJson) {
                JSONObject responseDiffsJsonObject = (JSONObject) JSONValue.parse(responseDiffObject.toString());

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSz");

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
