package com.kbsl.server.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class UserUpdateRequestDto {
    @Schema(description = "닉네임")
    private String nickName;
}
