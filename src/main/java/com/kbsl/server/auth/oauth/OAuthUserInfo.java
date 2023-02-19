package com.kbsl.server.auth.oauth;

public interface OAuthUserInfo {
    String getProviderId();
    String getProvider();
    String getUserName();
    String getImageUrl();
}
