package com.kbsl.server.user.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kbsl.server.auth.enums.ERole;
import lombok.*;

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
    private String username;
    private String imageUrl;
    @Enumerated(EnumType.STRING)
    private ERole eRole;

    @Builder
    public User(Long seq, String password, String username, String imageUrl, ERole eRole) {
        this.seq = seq;
        this.password = password;
        this.username = username;
        this.imageUrl = imageUrl;
        this.eRole = eRole;
    }
}
