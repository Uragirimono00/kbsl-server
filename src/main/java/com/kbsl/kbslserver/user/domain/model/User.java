package com.kbsl.kbslserver.user.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kbsl.kbslserver.auth.enums.ERole;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Table(name = "tb_user")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    @JsonIgnore
    private String password;
    private String nickname;
    private String imageUrl;
    @Enumerated(EnumType.STRING)
    private ERole eRole;

    @Builder
    public User(Long seq, String password, String nickname, String imageUrl, ERole eRole) {
        this.seq = seq;
        this.password = password;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.eRole = eRole;
    }
}
