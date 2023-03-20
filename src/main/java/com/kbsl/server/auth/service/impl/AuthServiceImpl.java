package com.kbsl.server.auth.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.kbsl.server.auth.domain.model.AuthToken;
import com.kbsl.server.auth.domain.repository.AuthTokenRepository;
import com.kbsl.server.auth.dto.request.AccessTokenRefreshTokenDto;
import com.kbsl.server.auth.dto.response.AccessTokenRefreshResponseDto;
import com.kbsl.server.auth.dto.response.AuthLoginResponse;
import com.kbsl.server.auth.dto.response.OauthTokenResponse;
import com.kbsl.server.auth.enums.ERole;
import com.kbsl.server.auth.oauth.OAuthUserInfo;
import com.kbsl.server.auth.oauth.provider.DiscordUserInfo;
import com.kbsl.server.auth.service.AuthService;
import com.kbsl.server.boot.exception.RestException;
import com.kbsl.server.boot.util.JwtUtils;
import com.kbsl.server.user.domain.model.User;
import com.kbsl.server.user.domain.repository.UserRepository;
import com.kbsl.server.user.service.principal.PrincipalUserDetail;
import com.kbsl.server.user.service.principal.PrincipalUserDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final InMemoryClientRegistrationRepository inMemoryClientRegistrationRepository;
    private final JwtUtils jwtUtils;
    private final PrincipalUserDetailService userDetailService;
    private final AuthTokenRepository authTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    /**
     * AccessToken Refresh
     * @param requestDto
     * @return
     */
    @Override
    @Transactional
    public AccessTokenRefreshResponseDto accessTokenRefresh(AccessTokenRefreshTokenDto requestDto) {
        requestDto.setAccessToken( jwtUtils.getAccessTokenFromBearer(
                requestDto.getAccessToken()
        ));

        String username = jwtUtils.getUserNameFromAccessToken(requestDto.getAccessToken());

        if(!userRepository.existsByUsername(username)) {
            throw new UsernameNotFoundException(username);
        }

        if(!authTokenRepository.existsByAccessTokenAndSeq(requestDto.getAccessToken(), requestDto.getRefreshToken())) {
            log.error("Refresh Token이 만료되었습니다. 해당 토근 정보를 DB에서 제거합니다.");
            authTokenRepository.deleteBySeq(requestDto.getAccessToken());
            throw new RestException(HttpStatus.valueOf(401), "Refresh Token이 만료되었습니다. 해당 토큰 정보를 DB에서 제거합니다.");
        }

        PrincipalUserDetail userDetail = userDetailService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());

        String newAccessToken = jwtUtils.generateAccessToken(authenticationToken);

        AuthToken authTokenEntity = authTokenRepository.findBySeq(requestDto.getRefreshToken()).orElseThrow(
                () -> new RestException(HttpStatus.BAD_REQUEST, "해당 Refresh Token을 찾을 수 없습니다.")
        );

        authTokenEntity.updateAccessToken(newAccessToken);

        return AccessTokenRefreshResponseDto.builder()
                .accessToken(newAccessToken)
                .build();
    }

    /**
     * Oauth2Login main
     * @param code
     * @return
     */
    @Override
    @Transactional
    public AuthLoginResponse authLogin(String code, String requestPath) {
        String provierName = getProvider(requestPath);

        ClientRegistration provider = inMemoryClientRegistrationRepository.findByRegistrationId(provierName);;

        log.info(code);

        OauthTokenResponse oauthTokenResponse = getToken(code, provider);

        Authentication authentication = getOAuthUserInfo(provierName, oauthTokenResponse, provider);;

        PrincipalUserDetail userDetail = (PrincipalUserDetail) authentication.getPrincipal();
        Set<String> authorities = userDetail.getAuthorities()
                .stream().map(role -> role.getAuthority())
                .collect(Collectors.toSet());

        String accessToken = jwtUtils.generateAccessToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);

        String fakeRefreshToken = passwordEncoder.encode(refreshToken);

        AuthToken authTokenEntity = AuthToken.builder()
                .seq(fakeRefreshToken)
                .userSeq(userDetail.getUserSeq())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        if(!authorities.contains("ROLE_ADMIN")) {
            Boolean isLogin = authTokenRepository.existsByUserSeq(userDetail.getUserSeq());
            if(isLogin) {
                log.info("기존에 로그인된 일반 사용자입니다. DB값을 추출후 재삽입합니다.");
                authTokenRepository.deleteByUserSeq(userDetail.getUserSeq());
            }
        } else {
            log.info("관리자는 중복 로그인 체크를 하지 않습니다.");
        }

        authTokenRepository.save(authTokenEntity);
        AuthLoginResponse authLoginResponse = AuthLoginResponse.builder()
                .userSeq(userDetail.getUserSeq())
                .eRole(userDetail.getERole())
                .accessToken(accessToken)
                .userName(userDetail.getUsername())
                .refreshToken(refreshToken)
                .imageUrl(userDetail.getImageUrl())
                .build();

        return authLoginResponse;
    }

    /**
     * 리퀘스트 패스 받아와 프로바이더 값 설정
     * @param requestPath
     * @return
     */
    public String getProvider(String requestPath) {
        return requestPath.split("/")[2];
    }

    /**
     * 로그아웃 및 토큰 정보 지우기
     * @param accessToken
     * @return
     */
    @Override
    @Transactional
    public Boolean logOut(String accessToken) {
        PrincipalUserDetail userDetail = (PrincipalUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        accessToken = accessToken.replace("Bearer ", "");
        Integer deletedCnt = authTokenRepository.deleteByAccessToken(accessToken);
        log.info("AccessToken이 {}개 제거되었습니다", deletedCnt);

        SecurityContextHolder.clearContext();
        return true;
    }

    /**
     * 토큰으로 유저정보 제대로 가져와지나 테스트
     * @param
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public String getUserName() {
        PrincipalUserDetail userDetail = (PrincipalUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetail.getUserSeq().toString() + " : " + userDetail.getERole();
    }

    /**
     * 가져온 유저 정보 세팅
     * @param providerName
     * @param oauthTokenResponse
     * @param provider
     * @return
     */
    private Authentication getOAuthUserInfo(String providerName, OauthTokenResponse oauthTokenResponse, ClientRegistration provider) {
        Map<String, Object> oauthUserAttributes = getOAuthUserAttributes(provider, oauthTokenResponse);
        OAuthUserInfo oAuthUserInfo = null;
        log.info(oauthUserAttributes.toString());
        if(providerName.equals("discord")) {
            oAuthUserInfo = new DiscordUserInfo(oauthUserAttributes);
        } else {
            log.error("허용되지 않는 접근입니다.");
        }

        log.info("oauth.getUsername {}", oAuthUserInfo.getUserName());
        String provide = oAuthUserInfo.getProvider();
        String providerId = oAuthUserInfo.getProviderId();
        String username = oAuthUserInfo.getUserName();
        String imageUrl = oAuthUserInfo.getImageUrl();
        String password = provide + providerId;

        User user = userRepository.findByUsername(username).orElse(null);

        if(user == null) {
            userRepository.save(User.builder()
                    .password(passwordEncoder.encode(password))
                    .username(username)
                    .eRole(ERole.ROLE_USER)
                    .imageUrl(imageUrl)
                    .build()
            );
        }
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }

    /**
     * oauth 토큰으로 인증후 데이터 가져오기
     * @param provider
     * @param oauthTokenResponse
     * @return
     */
    private Map<String, Object> getOAuthUserAttributes(ClientRegistration provider, OauthTokenResponse oauthTokenResponse) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(provider.getProviderDetails().getUserInfoEndpoint().getUri())
                .addHeader("Authorization", "Bearer " + oauthTokenResponse.getAccess_token())
                .build();

        try(Response response = client.newCall(request).execute()) {
            String responseResult = response.body().string();

            response.close();
            JSONObject responseFromJson = new JSONObject(responseResult);

            Map<String, Object> responseMap = new ObjectMapper().readValue(responseFromJson.toString(), Map.class);

            return responseMap;
        } catch (IOException e) {
            throw new RestException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * oauth 이용 토큰 생성
     * @param code
     * @param provider
     * @return
     */
    private OauthTokenResponse getToken(String code, ClientRegistration provider) {
        OkHttpClient client = new OkHttpClient();
        Map<String, Object> requestMap = tokenRequest(code, provider);
        String requestParam = "grant_type=authorization_code&client_id="+requestMap.get("client_id")+"&redirect_uri="+
                requestMap.get("redirect_uri")+"&client_secret="+requestMap.get("client_secret")+"&code="+code;
        RequestBody requestBody = RequestBody.create(requestParam, MediaType.parse("application/x-www-form-urlencoded;charset=utf-8"));

        Request request = new Request.Builder()
                .url(provider.getProviderDetails().getTokenUri())
                .post(requestBody)
                .build();

        try(Response response = client.newCall(request).execute()) {
            String responseResult = response.body().string();
            response.close();
            JSONObject responseFromJson = new JSONObject(responseResult);

            OauthTokenResponse oauthTokenResponse = new Gson().fromJson(responseFromJson.toString(), OauthTokenResponse.class);

            return oauthTokenResponse;
        } catch (IOException e) {
            throw new RestException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * oauth 서버에 보낼 Request 정보
     * @param code
     * @param provider
     * @return
     */
    private Map<String, Object> tokenRequest(String code, ClientRegistration provider) {
        Map<String, Object> formData = new LinkedHashMap<>();

        formData.put("code", code);
        formData.put("grant_type", "authorization_code");
        formData.put("redirect_uri", provider.getRedirectUri());
        formData.put("client_secret", provider.getClientSecret());
        formData.put("client_id", provider.getClientId());

        return formData;
    }
}