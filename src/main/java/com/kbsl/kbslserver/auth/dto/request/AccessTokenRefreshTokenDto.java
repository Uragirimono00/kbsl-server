package com.kbsl.kbslserver.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AccessTokenRefreshTokenDto {
    private String accessToken;
    private String refreshToken;
}
