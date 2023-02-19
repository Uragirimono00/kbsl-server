package com.kbsl.kbslserver.auth.oauth;

public interface OAuthUserInfo {
    String getProviderId();
    String getProvider();
    String getNickname();
    String getImageUrl();
}
