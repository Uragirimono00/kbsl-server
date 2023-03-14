package com.kbsl.server.score.domain.model;

import com.kbsl.server.boot.domain.model.BaseEntity;
import com.kbsl.server.song.domain.model.Song;
import com.kbsl.server.user.domain.model.User;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@Table(name = "tb_score")
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
public class Score extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userSeq")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "songSeq")
    private Song song;

    private Long scoreSeq;

    private Long baseScore;

    private Long modifiedScore;

    private Double accuracy;

    private Integer badCut;

    private Integer missedNote;

    private Integer bombCut;

    private Integer wallsHit;

    private Integer pause;

    private Integer playCount;

    private Double accLeft;

    private Double accRight;

    private String comment;

    private Integer timePost;

}
