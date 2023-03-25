package com.kbsl.server.boot.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DiscordUtils {

    @Value("${webhook.discord.league}")
    private static String LeagueChannelUrl;

    public static void LeagueCreateMessage(String content){
        String discordWebhookUrl = LeagueChannelUrl;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> message = new HashMap<>();
        message.put("content", content);

        log.info(discordWebhookUrl, content);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(message, headers);
        restTemplate.postForObject(discordWebhookUrl, request, String.class);
    }
}
