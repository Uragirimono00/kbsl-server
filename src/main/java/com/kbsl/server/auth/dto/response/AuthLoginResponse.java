package com.kbsl.server.auth.dto.response;

import com.kbsl.server.auth.enums.ERole;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthLoginResponse {
    private Long userSeq;
    private String userName;
    private ERole eRole;
    private String tokenType = "Bearer ";
    private String accessToken;
    private String refreshToken;
    private String imageUrl;

    @Builder
    public AuthLoginResponse(Long userSeq, String userName, ERole eRole, String accessToken, String refreshToken, String imageUrl) {
        this.userSeq = userSeq;
        this.userName = userName;
        this.eRole = eRole;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.imageUrl = imageUrl;
    }
}
