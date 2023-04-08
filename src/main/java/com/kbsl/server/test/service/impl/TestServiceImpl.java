package com.kbsl.server.test.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbsl.server.boot.discord.Author;
import com.kbsl.server.boot.discord.DiscordEmbed;
import com.kbsl.server.boot.discord.DiscordMessage;
import com.kbsl.server.boot.util.DiscordUtils;
import com.kbsl.server.test.service.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final DiscordUtils discordUtils;

    @Override
    public Object discordEmbedTest() throws Exception {
        Author author = Author.builder()
                .name("김캬루")
                .iconUrl("https://media.discordapp.net/attachments/1030367806644035626/1094182606700027924/2.png?width=896&height=896")
                .url("https://naver.com")
                .build();

        List<DiscordEmbed> discordEmbed = new ArrayList<>();
        discordEmbed.add(
                DiscordEmbed.builder()
                        .author(author)
                        .title("테스트입니다.")
                        .description("설명입니다.")
                        .color(0xFF0000)
                        .build()
        );

        DiscordMessage discordMessage = DiscordMessage.builder()
                .content("멍청해")
                .embeds(discordEmbed)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String json = objectMapper.writeValueAsString(discordMessage);
            System.out.println(json);

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String url = "https://discord.com/api/webhooks/1094181987390062622/4mvFIUJLwUijnIg0wnEkbRw7ycBmDc-8ZF7_QrRjgetwoxq9rQw3FT0TVdixQud4y1-C";
            HttpEntity<?> request = new HttpEntity<>(json, headers);
            restTemplate.postForObject(url, request, String.class);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

//        discordUtils.sendEmbedMessage(discordEmbed);

        return null;
    }
}
