package com.kbsl.server.user.service.impl;

import com.google.gson.JsonObject;
import com.kbsl.server.auth.domain.model.AuthToken;
import com.kbsl.server.auth.domain.repository.AuthTokenRepository;
import com.kbsl.server.boot.exception.RestException;
import com.kbsl.server.user.domain.model.User;
import com.kbsl.server.user.domain.repository.UserRepository;
import com.kbsl.server.user.dto.request.UserUpdateRequestDto;
import com.kbsl.server.user.dto.response.UserResponseDto;
import com.kbsl.server.user.service.UserService;
import com.kbsl.server.user.service.principal.PrincipalUserDetail;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.JSONValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AuthTokenRepository authTokenRepository;

    @Override
    @Transactional
    public UserResponseDto findDetailUser(Long userSeq) {
        User userEntity = userRepository.findBySeq(userSeq)
                .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 유저를 찾을 수 없습니다."));

        UserResponseDto responseDto = UserResponseDto.builder().entity(userEntity).build();

        return responseDto;
    }

    @Override
    @Transactional
    public UserResponseDto updateSteamIdWithBeatLeader(Long userSeq, UserUpdateRequestDto userUpdateRequestDto) {
        User userEntity = userRepository.findBySeq(userSeq)
                .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 유저를 찾을 수 없습니다."));
        if (userRepository.existsBySteamId(userUpdateRequestDto.getSteamId()))
            throw new RestException(HttpStatus.BAD_REQUEST, "이미 존재하는 유저입니다. https://www.beatleader.xyz/u/" + userUpdateRequestDto.getSteamId());

        /**
         * 작성자와 요청자의 시퀀스가 일치하는지 확인한다. 그렇지 않을 경우, 예외를 발생시킨다.
         */
        PrincipalUserDetail userDetails = (PrincipalUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        validateUser(userDetails, userEntity);

        try {
            Long.parseLong(userUpdateRequestDto.getSteamId());
        }catch (NumberFormatException e){
            throw new RestException(HttpStatus.BAD_REQUEST, "steamId는 숫자로 되어있어야 합니다. steamId = " + userUpdateRequestDto.getSteamId());
        }

        URI uri = UriComponentsBuilder
                .fromUriString("https://api.beatleader.xyz")
                .pathSegment("player", userUpdateRequestDto.getSteamId())
                .encode()
                .build()
                .toUri();

        log.info("Request URI: " + uri);

        RestTemplate restTemplate = new RestTemplate();
        String response;
        try {
            response = restTemplate.getForObject(uri, String.class);
        }catch (Exception e){
            throw new RestException(HttpStatus.BAD_REQUEST, "잘못된 JSON 응답입니다. steamId = " + userUpdateRequestDto.getSteamId());
        }

        log.info(response);

        /**
         * BeatLeader 데이터가 존재하지 않을경우 패스한다.
         */
        JSONObject responseJson = (JSONObject) JSONValue.parse(response);
        if (responseJson == null) {
            log.error("잘못된 JSON 응답입니다. BeatLeader API: " + response);
            throw new RestException(HttpStatus.BAD_REQUEST, "잘못된 JSON 응답입니다. steamId = " + userUpdateRequestDto.getSteamId());
        }

        /**
         * 이미 등록된 점수의 경우 패스한다.
         */
        String country = responseJson.get("country").toString();
        if (!country.equals("KR")) {
            throw new RestException(HttpStatus.BAD_REQUEST, "국적이 한국이 아닙니다. https://www.beatleader.xyz/u/" + userUpdateRequestDto.getSteamId());
        }

        /**
         * 수정 후 리스폰스 엔티티에 담아 리턴
         */
        userEntity.update(userUpdateRequestDto);
        UserResponseDto responseDto = UserResponseDto.builder().entity(userEntity).build();

        return responseDto;

    }

    @Override
    @Transactional
    public UserResponseDto updateSteamId(Long userSeq, UserUpdateRequestDto userUpdateRequestDto) throws Exception {
        User userEntity = userRepository.findBySeq(userSeq)
                .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 유저를 찾을 수 없습니다."));
        if (userRepository.existsBySteamId(userUpdateRequestDto.getSteamId()))
            throw new RestException(HttpStatus.BAD_REQUEST, "이미 존재하는 유저입니다. https://www.beatleader.xyz/u/" + userUpdateRequestDto.getSteamId());

        /**
         * 작성자와 요청자의 시퀀스가 일치하는지 확인한다. 그렇지 않을 경우, 예외를 발생시킨다.
         */
        PrincipalUserDetail userDetails = (PrincipalUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        validateUser(userDetails, userEntity);

        try {
            Long.parseLong(userUpdateRequestDto.getSteamId());
        }catch (NumberFormatException e){
            throw new RestException(HttpStatus.BAD_REQUEST, "steamId는 숫자로 되어있어야 합니다. steamId = " + userUpdateRequestDto.getSteamId());
        }

        /**
         * 수정 후 리스폰스 엔티티에 담아 리턴
         */
        userEntity.update(userUpdateRequestDto);
        UserResponseDto responseDto = UserResponseDto.builder().entity(userEntity).build();

        return responseDto;    }

    @Override
    @Transactional
    public UserResponseDto findAtInfo(String authorization) throws Exception {
        AuthToken authTokenEntity = authTokenRepository.findByAccessToken(authorization)
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 유저를 찾을 수 없습니다."));

        User userEntity = userRepository.findBySeq(authTokenEntity.getUserSeq())
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 유저를 찾을 수 없습니다."));

        return UserResponseDto.builder().entity(userEntity).build();
    }

    /**
     * API 요청자와 작성자를 비교한다.
     *
     * @param userDetails
     * @param userEntity
     * @return
     * @throws Exception
     */
    private void validateUser(PrincipalUserDetail userDetails, User userEntity) {
        if (userDetails.getUserSeq() != userEntity.getSeq()) {
            throw new RestException(HttpStatus.BAD_REQUEST, "요청자와 작성자가 일치하지 않습니다. userSeq=" + userDetails.getUserSeq());
        }
    }
}
