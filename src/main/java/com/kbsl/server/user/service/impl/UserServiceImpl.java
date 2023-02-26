package com.kbsl.server.user.service.impl;

import com.google.gson.JsonObject;
import com.kbsl.server.boot.exception.RestException;
import com.kbsl.server.user.domain.model.User;
import com.kbsl.server.user.domain.repository.UserRepository;
import com.kbsl.server.user.dto.request.UserUpdateRequestDto;
import com.kbsl.server.user.dto.response.UserResponseDto;
import com.kbsl.server.user.service.UserService;
import com.kbsl.server.user.service.principal.PrincipalUserDetail;
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

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

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
    public UserResponseDto updateUser(Long userSeq, UserUpdateRequestDto userUpdateRequestDto) {
        User userEntity = userRepository.findBySeq(userSeq)
                .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 유저를 찾을 수 없습니다."));
        if(userRepository.existsBybeatleaderId(userUpdateRequestDto.getBeatleaderId()))
            throw new RestException(HttpStatus.BAD_REQUEST, "이미 존재하는 유저입니다. https://www.beatleader.xyz/u/" + userUpdateRequestDto.getBeatleaderId());

        /**
         * 작성자와 요청자의 시퀀스가 일치하는지 확인한다. 그렇지 않을 경우, 예외를 발생시킨다.
         */
        PrincipalUserDetail userDetails = (PrincipalUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        validateUser(userDetails, userEntity);

        try {
            String url = "https://api.beatleader.xyz/player/" + userUpdateRequestDto.getBeatleaderId();
            RestTemplate restTemplate = new RestTemplate();

            // create headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // create param
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("stats ",true);

            HttpEntity<String> entity = new HttpEntity<String>(jsonObject.toString(), headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            log.info(response.getBody()+"");
            log.info(response.getStatusCodeValue()+"");

            //에러처리해야댐
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.info("error");
            log.info(e.toString());
            throw new RestException(HttpStatus.BAD_REQUEST, "존재하지 않는 Beatleader ID 입니다.");
        }
        catch (Exception e) {
            log.info(e.toString());
        }

        /**
         * 수정 후 리스폰스 엔티티에 담아 리턴
         */
        userEntity.update(userUpdateRequestDto);
        UserResponseDto responseDto = UserResponseDto.builder().entity(userEntity).build();

        return null;
    }

    /**
     * API 요청자와 작성자를 비교한다.
     * @param userDetails
     * @param userEntity
     * @return
     * @throws Exception
     */
    private void validateUser(PrincipalUserDetail userDetails, User userEntity) {
        if(userDetails.getUserSeq() != userEntity.getSeq()) {
            throw new RestException(HttpStatus.BAD_REQUEST, "요청자와 작성자가 일치하지 않습니다. userSeq=" + userDetails.getUserSeq());
        }
    }
}
