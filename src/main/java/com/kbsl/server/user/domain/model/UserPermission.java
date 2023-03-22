package com.kbsl.server.user.domain.model;

import com.kbsl.server.boot.domain.model.BaseEntity;
import com.kbsl.server.user.enums.UserPermissionType;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.List;

@Slf4j
@Entity
@Table(name = "tb_user_permission")
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
public class UserPermission extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Enumerated(EnumType.STRING)
    private UserPermissionType userPermissionType;

    @OneToMany(mappedBy = "permission")
    private List<UserPermissionList> userPermissionList;
}
