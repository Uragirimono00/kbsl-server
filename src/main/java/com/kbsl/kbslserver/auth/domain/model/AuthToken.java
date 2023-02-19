package com.kbsl.kbslserver.auth.domain.model;

import com.kbsl.kbslserver.boot.domain.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

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

    @Column(columnDefinition = "varchar(1000)")
    private String accessToken;

    @Column(columnDefinition = "varchar(1000)")
    private String refreshToken;

    public void updateAccessToken(String newAccessToken) {
        this.accessToken = newAccessToken;
    }
}

