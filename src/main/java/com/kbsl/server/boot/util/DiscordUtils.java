package com.kbsl.server.boot.util;

import com.kbsl.server.boot.discord.DiscordEmbed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class DiscordUtils {

    @Value("${webhook.discord.league}")
    private String leagueChannelUrl;

    @Value("${webhook.discord.score}")
    private String scoreChannelUrl;

    public void sendEmbedMessage(DiscordEmbed discordEmbed) {
        try{
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<DiscordEmbed> request = new HttpEntity<>(discordEmbed, headers);
            restTemplate.postForObject(scoreChannelUrl, request, String.class);
        }catch (Exception e){
            log.info(e.toString());
        }
    }

    public void LeagueCreateMessage(String content){
        try{
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> message = new HashMap<>();
            message.put("content", content);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(message, headers);
            restTemplate.postForObject(leagueChannelUrl, request, String.class);
        }catch (Exception e){
            log.info(e.toString());
        }
    }

    public void sendMessage(String content){
        try{
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> message = new HashMap<>();
            message.put("content", content);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(message, headers);
            restTemplate.postForObject(leagueChannelUrl, request, String.class);
        }catch (Exception e){
            log.info(e.toString());
        }
    }

}
