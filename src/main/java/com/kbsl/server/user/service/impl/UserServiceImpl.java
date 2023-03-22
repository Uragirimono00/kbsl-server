package com.kbsl.server.user.service.impl;

import com.kbsl.server.auth.domain.model.AuthToken;
import com.kbsl.server.auth.domain.repository.AuthTokenRepository;
import com.kbsl.server.boot.exception.RestException;
import com.kbsl.server.user.domain.model.User;
import com.kbsl.server.user.domain.model.UserPermission;
import com.kbsl.server.user.domain.model.UserPermissionList;
import com.kbsl.server.user.domain.repository.UserPermissionListRepository;
import com.kbsl.server.user.domain.repository.UserPermissionRepository;
import com.kbsl.server.user.domain.repository.UserRepository;
import com.kbsl.server.user.dto.request.UserSteamIdUpdateRequestDto;
import com.kbsl.server.user.dto.request.UserUpdateRequestDto;
import com.kbsl.server.user.dto.response.UserDetailResponseDto;
import com.kbsl.server.user.dto.response.UserResponseDto;
import com.kbsl.server.user.enums.UserPermissionType;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AuthTokenRepository authTokenRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final UserPermissionListRepository userPermissionListRepository;

    /**
     * 특정 유저의 정보를 조회한다!
     * @param userSeq
     * @return
     */
    @Override
    @Transactional
    public UserDetailResponseDto findDetailUser(Long userSeq) {
        User userEntity = userRepository.findBySeq(userSeq)
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 유저를 찾을 수 없습니다."));

        UserDetailResponseDto responseDto = UserDetailResponseDto.builder().entity(userEntity).build();

        return responseDto;
    }

    /**
     * 특정 유저의 SteamId를 변경한다.
     * 단, Beatleader의 데이터로 변경하기 때문에 Beatleader에서 데이터 조회후 유저가 없을경우 예외를 발생시킨다.
     * @param userSeq
     * @param userSteamIdUpdateRequestDto
     * @return
     */
    @Override
    @Transactional
    public UserResponseDto updateSteamIdWithBeatLeader(Long userSeq, UserSteamIdUpdateRequestDto userSteamIdUpdateRequestDto) {
        User userEntity = userRepository.findBySeq(userSeq)
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 유저를 찾을 수 없습니다."));
        if (userRepository.existsBySteamId(userSteamIdUpdateRequestDto.getSteamId()))
            throw new RestException(HttpStatus.BAD_REQUEST, "이미 존재하는 유저입니다. https://www.beatleader.xyz/u/" + userSteamIdUpdateRequestDto.getSteamId());

        /**
         * 작성자와 요청자의 시퀀스가 일치하는지 확인한다. 그렇지 않을 경우, 예외를 발생시킨다.
         */
        PrincipalUserDetail userDetails = (PrincipalUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        validateUser(userDetails, userEntity);

        try {
            Long.parseLong(userSteamIdUpdateRequestDto.getSteamId());
        } catch (NumberFormatException e) {
            throw new RestException(HttpStatus.BAD_REQUEST, "steamId는 숫자로 되어있어야 합니다. steamId = " + userSteamIdUpdateRequestDto.getSteamId());
        }

        URI uri = UriComponentsBuilder
            .fromUriString("https://api.beatleader.xyz")
            .pathSegment("player", userSteamIdUpdateRequestDto.getSteamId())
            .encode()
            .build()
            .toUri();

        log.info("Request URI: " + uri);

        RestTemplate restTemplate = new RestTemplate();
        String response;
        try {
            response = restTemplate.getForObject(uri, String.class);
        } catch (Exception e) {
            throw new RestException(HttpStatus.BAD_REQUEST, "잘못된 JSON 응답입니다. steamId = " + userSteamIdUpdateRequestDto.getSteamId());
        }

        log.info(response);

        /**
         * BeatLeader 데이터가 존재하지 않을경우 패스한다.
         */
        JSONObject responseJson = (JSONObject) JSONValue.parse(response);
        if (responseJson == null) {
            log.error("잘못된 JSON 응답입니다. BeatLeader API: " + response);
            throw new RestException(HttpStatus.BAD_REQUEST, "잘못된 JSON 응답입니다. steamId = " + userSteamIdUpdateRequestDto.getSteamId());
        }

        /**
         * 이미 등록된 점수의 경우 패스한다.
         */
        String country = responseJson.get("country").toString();
        if (!country.equals("KR")) {
            throw new RestException(HttpStatus.BAD_REQUEST, "국적이 한국이 아닙니다. https://www.beatleader.xyz/u/" + userSteamIdUpdateRequestDto.getSteamId());
        }

        /**
         * 수정 후 리스폰스 엔티티에 담아 리턴
         */
        userEntity.steamIdUpdate(userSteamIdUpdateRequestDto);
        UserResponseDto responseDto = UserResponseDto.builder().entity(userEntity).build();

        return responseDto;

    }

    /**
     * 특정 유저의 SteamId를 변경한다.
     * Steam에서 로그인한 정보를 토대로 변경을 시도한다.
     * @param userSeq
     * @param userSteamIdUpdateRequestDto
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public UserResponseDto updateSteamId(Long userSeq, UserSteamIdUpdateRequestDto userSteamIdUpdateRequestDto) throws Exception {
        User userEntity = userRepository.findBySeq(userSeq)
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 유저를 찾을 수 없습니다."));

        if (userRepository.existsBySteamId(userSteamIdUpdateRequestDto.getSteamId())) {
            throw new RestException(HttpStatus.BAD_REQUEST, "이미 존재하는 유저입니다. https://www.beatleader.xyz/u/" + userSteamIdUpdateRequestDto.getSteamId());
        }

        /**
         * 작성자와 요청자의 시퀀스가 일치하는지 확인한다. 그렇지 않을 경우, 예외를 발생시킨다.
         */
        PrincipalUserDetail userDetails = (PrincipalUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        validateUser(userDetails, userEntity);

        try {
            Long.parseLong(userSteamIdUpdateRequestDto.getSteamId());
        } catch (NumberFormatException e) {
            throw new RestException(HttpStatus.BAD_REQUEST, "steamId는 숫자로 되어있어야 합니다. steamId = " + userSteamIdUpdateRequestDto.getSteamId());
        }

        /**
         * 수정 후 리스폰스 엔티티에 담아 리턴
         */
        userEntity.steamIdUpdate(userSteamIdUpdateRequestDto);
        UserResponseDto responseDto = UserResponseDto.builder().entity(userEntity).build();

        return responseDto;
    }

    /**
     * 유저의 개인정보를 수정한다.
     * 현재는 닉네임만 수정가능
     * @param userSeq
     * @param userUpdateRequestDto
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public UserResponseDto updateUser(Long userSeq, UserUpdateRequestDto userUpdateRequestDto) throws Exception {
        User userEntity = userRepository.findBySeq(userSeq)
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 유저를 찾을 수 없습니다."));

        /**
         * 작성자와 요청자의 시퀀스가 일치하는지 확인한다. 그렇지 않을 경우, 예외를 발생시킨다.
         */
        PrincipalUserDetail userDetails = (PrincipalUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        validateUser(userDetails, userEntity);

        /**
         * 수정 후 리스폰스 엔티티에 담아 리턴
         */
        userEntity.update(userUpdateRequestDto);
        UserResponseDto responseDto = UserResponseDto.builder().entity(userEntity).build();

        return responseDto;
    }

    /**
     * 유저의 추가 권한을 부여한다.
     * 유저가 없거나 권한이 없을 경우 예외를 발생시킨다.
     * @param userSeq
     * @param userPermissionType
     * @return
     */
    @Override
    @Transactional
    public UserDetailResponseDto createPermissionUser(Long userSeq, UserPermissionType userPermissionType) {
        User userEntity = userRepository.findBySeq(userSeq)
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 유저를 찾을 수 없습니다."));

        UserPermission userPermissionEntity = userPermissionRepository.findByUserPermissionType(userPermissionType)
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 권한을 찾을 수 없습니다."));

        if (userPermissionListRepository.existsByUserAndPermission(userEntity, userPermissionEntity))
            throw new RestException(HttpStatus.CONFLICT, userEntity.getNickName() + " 유저는 이미 " + userPermissionType + " 권한을 가지고 있습니다. ");

        userPermissionListRepository.save(UserPermissionList.builder()
            .user(userEntity)
            .permission(userPermissionEntity)
            .build()
        );

        return UserDetailResponseDto.builder().entity(userEntity).build();
    }

    /**
     * 유저의 추가권한을 삭제한다.
     * 유저가 없거나 권한이 없을 경우 예외를 발생시킨다.
     * @param userSeq
     * @param userPermissionType
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public UserDetailResponseDto deletePermissionUser(Long userSeq, UserPermissionType userPermissionType) throws Exception {
        User userEntity = userRepository.findBySeq(userSeq)
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 유저를 찾을 수 없습니다."));

        UserPermission userPermissionEntity = userPermissionRepository.findByUserPermissionType(userPermissionType)
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 권한을 찾을 수 없습니다."));

        UserPermissionList userPermissionListEntity = userPermissionListRepository.findByUserAndPermission(userEntity, userPermissionEntity)
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, userEntity.getNickName() + " 유저의 " + userPermissionType +" 권한을 찾을 수 없습니다."));

        /**
         * Response Dto 생성 및 삭제된 permission 엔티티 제거
         */
        userEntity.getUserPermissionList().remove(userPermissionListEntity);

        userPermissionListRepository.delete(userPermissionListEntity);

        return UserDetailResponseDto.builder().entity(userEntity).build();
    }

    /**
     * 현재의 AT토큰으로 유저를 조회한다.
     * @param authorization
     * @return
     * @throws Exception
     */
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
