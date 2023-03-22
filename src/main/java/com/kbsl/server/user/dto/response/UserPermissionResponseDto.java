package com.kbsl.server.user.dto.response;

import com.kbsl.server.user.domain.model.UserPermission;
import com.kbsl.server.user.domain.model.UserPermissionList;
import com.kbsl.server.user.enums.UserPermissionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.stream.Collectors;

@Getter
@ToString
@NoArgsConstructor
public class UserPermissionResponseDto {

    @Schema(description = "추가 권한")
    private UserPermissionType userPermissionType;

    @Builder
    public UserPermissionResponseDto(UserPermissionList entity) {
        this.userPermissionType = entity.getPermission().getUserPermissionType();
    }
}
