package com.kbsl.server.boot.util;

import com.kbsl.server.auth.enums.ERole;
import com.kbsl.server.boot.exception.RestException;
import com.kbsl.server.user.domain.model.User;
import com.kbsl.server.user.domain.repository.UserRepository;
import com.nimbusds.jose.shaded.json.JSONValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;

import java.net.URI;

@Component
@Slf4j
@RequiredArgsConstructor
public class SteamUtils {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private String steamApiUrl = "https://api.steampowered.com";
    private String wepApiKey = "5C079DD9A9BFF5F7040586E555524427";
    private String appId = "620980";
    public Authentication getAuth(String ticket) {

        URI uri = UriComponentsBuilder
            .fromUriString(steamApiUrl)
            .pathSegment("ISteamUserAuth", "AuthenticateUserTicket", "v1")
            .queryParam("key", wepApiKey)
            .queryParam("appid", appId)
            .queryParam("ticket", ticket)
//            .queryParam("identity", identity)
            .encode()
            .build()
            .toUri();
        log.info("Request URI: " + uri);

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(uri, String.class);
//        response = "{\"response\":{\"params\":{\"result\":\"OK\",\"steamid\":\"76561198115176242\",\"ownersteamid\":\"76561198115176242\",\"vacbanned\":false,\"publisherbanned\":false}}}";
        log.info("Response: " + response);

        JSONObject responseJson = (JSONObject) JSONValue.parse(response);
        if (responseJson == null) {
            throw new RestException(HttpStatus.BAD_REQUEST, "잘못된 JSON 응답입니다. Steam API: " + response);
        }
        JSONObject responseJsonData = (JSONObject) JSONValue.parse(responseJson.get("response").toString());

        if(responseJsonData.get("params") == null){
            throw new RestException(HttpStatus.BAD_REQUEST, "유효한 Ticket이 아닙니다.");
        }
        JSONObject responseParamsJson = (JSONObject) JSONValue.parse(responseJsonData.get("params").toString());

        log.info("" + responseParamsJson.get("result").toString());
        log.info("" + responseParamsJson.get("ownersteamid").toString());

        if (responseParamsJson.get("result").toString().equals("OK") && !responseParamsJson.get("ownersteamid").toString().isEmpty()) {
            return getSteamUserInfo(responseParamsJson.get("ownersteamid").toString());
        }else{
            return null;
        }
    }

    private Authentication getSteamUserInfo(String ownersteamid) {
        URI uri = UriComponentsBuilder
            .fromUriString(steamApiUrl)
            .pathSegment("ISteamUser", "GetPlayerSummaries", "v2")
            .queryParam("key", wepApiKey)
            .queryParam("steamids", ownersteamid)
            .encode()
            .build()
            .toUri();

        log.info("Request URI: " + uri);

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(uri, String.class);
        log.info("Response: " + response);

        JSONObject responseJson = (JSONObject) JSONValue.parse(response);
        if (responseJson == null) {
            throw new RestException(HttpStatus.BAD_REQUEST, "잘못된 JSON 응답입니다. Steam API: " + response);
        }

        JSONObject responseJsonData = (JSONObject) JSONValue.parse(responseJson.get("response").toString());
        JSONArray responsePlayerJson = (JSONArray) JSONValue.parse(responseJsonData.get("players").toString());

        User user = userRepository.findByUsername(ownersteamid).orElse(null);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = null;

        for (Object responsePlayerObject : responsePlayerJson) {
            JSONObject responsePlayerJsonObject = (JSONObject) JSONValue.parse(responsePlayerObject.toString());

            String name = responsePlayerJsonObject.get("personaname").toString();
            String image = responsePlayerJsonObject.get("avatarfull").toString();
            String password = ownersteamid + "steam";

            if (user == null) {
                userRepository.save(User.builder()
                    .password(passwordEncoder.encode(password))
                    .username(ownersteamid)
                    .nickName(name)
                    .eRole(ERole.ROLE_USER)
                    .imageUrl(image)
                    .build()
                );
            }
            usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(ownersteamid, password);
        }

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }


}
