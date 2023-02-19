package com.kbsl.kbslserver.auth.oauth.provider;

import com.kbsl.kbslserver.auth.oauth.OAuthUserInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class DiscordUserInfo implements OAuthUserInfo {
    private Map<String, Object> attributes;

    public DiscordUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getProvider() {
        return "Discord";
    }

    @Override
    public String getNickname() {
        return String.valueOf(attributes.get("name"));
    }

    @Override
    public String getImageUrl() {
        return String.valueOf(attributes.get("picture"));
    }
}

