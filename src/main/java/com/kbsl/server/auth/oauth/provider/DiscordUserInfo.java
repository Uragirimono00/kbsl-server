package com.kbsl.server.auth.oauth.provider;

import com.kbsl.server.auth.oauth.OAuthUserInfo;
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
    public String getUserName() {
        return String.valueOf(attributes.get("username"));
    }

    @Override
    public String getImageUrl() {
        if(String.valueOf(attributes.get("avatar")).equals("null")){
            return "null";
        }else{
            return "https://cdn.discordapp.com/avatars/" + String.valueOf(attributes.get("id")) + "/" + String.valueOf(attributes.get("avatar")) + ".gif";
        }
    }
}

