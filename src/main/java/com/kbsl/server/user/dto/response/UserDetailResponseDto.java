package com.kbsl.server.user.dto.response;

import com.kbsl.server.auth.enums.ERole;
import com.kbsl.server.user.domain.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@NoArgsConstructor
public class UserDetailResponseDto {

    @Schema(description = "유저 시퀀스")
    private Long seq;

    @Schema(description = "유저 이름")
    private String userName;

    @Schema(description = "프로필 이미지")
    private String imageUrl;

    @Schema(description = "유저 권한")
    private ERole eRole;

    @Schema(description = "Steam ID")
    private String steamId;

    @Schema(description = "유저 권한")
    private List<UserPermissionResponseDto> userPermissionResponseList;

    @Builder
    public UserDetailResponseDto(User entity) {
        this.seq = entity.getSeq();
        this.userName = entity.getNickName();
        this.imageUrl = entity.getImageUrl();
        this.eRole = entity.getERole();
        this.steamId = entity.getSteamId();
        this.userPermissionResponseList = entity.getUserPermissionList().stream()
            .map(UserPermissionResponseDto::new)
            .collect(Collectors.toList());
    }

}
