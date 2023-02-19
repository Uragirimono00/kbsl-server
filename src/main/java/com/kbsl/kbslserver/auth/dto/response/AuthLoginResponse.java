package com.kbsl.kbslserver.auth.dto.response;

import com.kbsl.kbslserver.auth.enums.ERole;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthLoginResponse {
    private Long userSeq;
    private String nickname;
    private String email;
    private ERole eRole;
    private String tokenType = "Bearer ";
    private String accessToken;
    private String refreshToken;
    private String imageUrl;
    private Integer emailYn;

    @Builder
    public AuthLoginResponse(Long userSeq, String nickname, String email, ERole eRole, String accessToken, String refreshToken, String imageUrl, Integer emailYn) {
        this.userSeq = userSeq;
        this.nickname = nickname;
        this.email = email;
        this.eRole = eRole;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.imageUrl = imageUrl;
        this.emailYn = emailYn;
    }
}
