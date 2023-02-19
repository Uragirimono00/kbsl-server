package com.kbsl.kbslserver.auth.domain.model;

import com.kbsl.kbslserver.auth.enums.ERole;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;


@Entity
@ToString
@Table(name = "tb_role")
@NoArgsConstructor
@Getter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Enumerated(EnumType.STRING)
    private ERole role;

    @Builder
    public Role(ERole eRole) {
        this.role = eRole;
    }
}