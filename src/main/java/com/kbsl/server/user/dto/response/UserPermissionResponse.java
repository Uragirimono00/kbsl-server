package com.kbsl.server.user.dto.response;

import com.kbsl.server.auth.enums.ERole;
import com.kbsl.server.user.domain.model.User;
import com.kbsl.server.user.domain.model.UserPermission;
import com.kbsl.server.user.enums.UserPermissionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class UserPermissionResponse {

    @Schema(description = "추가 권한")
    private UserPermissionType userPermissionType;

    @Builder
    public UserPermissionResponse(UserPermission entity) {
        this.userPermissionType = entity.getUserPermissionType();
    }
}
