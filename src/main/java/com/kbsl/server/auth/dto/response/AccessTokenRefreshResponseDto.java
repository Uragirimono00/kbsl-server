package com.kbsl.server.auth.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessTokenRefreshResponseDto {
    private String accessToken;
}
