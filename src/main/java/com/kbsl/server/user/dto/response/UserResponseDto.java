package com.kbsl.server.user.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kbsl.server.auth.enums.ERole;
import com.kbsl.server.user.domain.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.metamodel.Metamodel;
import java.util.stream.Collectors;

@Getter
@ToString
@NoArgsConstructor
public class UserResponseDto {

    @Schema(description = "유저 시퀀스")
    private Long seq;

    @Schema(description = "유저 이름")
    private String username;

    @Schema(description = "프로필 이미지")
    private String imageUrl;

    @Schema(description = "유저 권한")
    private ERole eRole;

    @Schema(description = "비트리더 ID")
    private String beatleaderId;

    @Builder
    public UserResponseDto(User entity) {
        this.seq = entity.getSeq();
        this.username = entity.getUsername();
        this.imageUrl = entity.getImageUrl();
        this.eRole = entity.getERole();
        this.beatleaderId = entity.getBeatleaderId();
    }

}
