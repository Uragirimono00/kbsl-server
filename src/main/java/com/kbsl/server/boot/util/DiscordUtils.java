package com.kbsl.server.boot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kbsl.server.boot.discord.DiscordEmbed;
import com.kbsl.server.boot.discord.DiscordMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class DiscordUtils {

    @Value("${webhook.discord.league}")
    private String leagueChannelUrl;

    @Value("${webhook.discord.score}")
    private String scoreChannelUrl;

    public void sendEmbedMessage(DiscordMessage discordMessage) {
        discordMessage.getEmbeds().get(0).setTimestamp(ZonedDateTime.now(ZoneId.of("UTC")).toLocalDateTime());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:sss"));

        try {
            String json = objectMapper.writeValueAsString(discordMessage);
            log.info(json);

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(json, headers);
            restTemplate.postForObject(scoreChannelUrl, request, String.class);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
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
