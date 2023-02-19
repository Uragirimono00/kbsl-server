package com.kbsl.kbslserver.auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OauthTokenResponse {
    private String token_type;
    private String access_token;
    private String refresh_token;
    private String refresh_token_expires_in;
    private String expires_in;
    private String scope;
}