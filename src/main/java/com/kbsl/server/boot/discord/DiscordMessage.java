package com.kbsl.server.boot.discord;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class DiscordMessage {

    @JsonProperty("content")
    private String content;

    private List<DiscordEmbed> embeds;
}
