package com.kbsl.server.user.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kbsl.server.auth.enums.ERole;
import com.kbsl.server.boot.domain.model.BaseEntity;
import com.kbsl.server.user.dto.request.UserSteamIdUpdateRequestDto;
import com.kbsl.server.user.dto.request.UserUpdateRequestDto;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;


@Entity
@Table(name = "tb_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    @JsonIgnore
    private String password;

    private String username;
    private String nickName;
    private String imageUrl;
    @Enumerated(EnumType.STRING)
    private ERole eRole;

    private String steamId;

    @Builder
    public User(Long seq, String password, String username, String imageUrl, ERole eRole, String steamId, String nickName) {
        this.seq = seq;
        this.password = password;
        this.username = username;
        this.imageUrl = imageUrl;
        this.eRole = eRole;
        this.steamId = steamId;
        this.nickName = steamId;
    }

    /**
     * 엔티티를 업데이트한다.
     * @param userSteamIdUpdateRequestDto
     */
    public void steamIdUpdate(UserSteamIdUpdateRequestDto userSteamIdUpdateRequestDto) {
        if (userSteamIdUpdateRequestDto.getSteamId() != null)
            this.steamId = userSteamIdUpdateRequestDto.getSteamId();
    }
    public void update(UserUpdateRequestDto userUpdateRequestDto) {
        if (userUpdateRequestDto.getNickName() != null)
            this.nickName = userUpdateRequestDto.getNickName();
    }


}
