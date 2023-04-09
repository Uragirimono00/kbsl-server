package com.kbsl.server.song.domain.model;

import com.kbsl.server.boot.domain.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "tb_song_badge_list")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
public class SongBadgeList extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Song song;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private SongBadge songBadge;
}
