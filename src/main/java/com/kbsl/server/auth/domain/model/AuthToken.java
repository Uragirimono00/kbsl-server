package com.kbsl.server.auth.domain.model;

import com.kbsl.server.boot.domain.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_user_auth")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
public class AuthToken extends BaseEntity {
    @Id
    private String seq;

    private Long userSeq;

    private String accessToken;

    private String refreshToken;

    public void updateAccessToken(String newAccessToken) {
        this.accessToken = newAccessToken;
    }
}
