package com.kbsl.server.boot.discord;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class DiscordEmbed {

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("color")
    private int color;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("author")
    private Author author;
}
