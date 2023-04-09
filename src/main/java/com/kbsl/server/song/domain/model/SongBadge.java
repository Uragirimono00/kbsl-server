package com.kbsl.server.song.domain.model;

import com.kbsl.server.boot.domain.model.BaseEntity;
import com.kbsl.server.song.enums.SongBadgeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Table(name = "tb_badge")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
public class SongBadge extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Enumerated(EnumType.STRING)
    private SongBadgeType songBadgeType;

    @OneToMany(mappedBy = "songBadge")
    private List<SongBadgeList> songBadgeList;
}
