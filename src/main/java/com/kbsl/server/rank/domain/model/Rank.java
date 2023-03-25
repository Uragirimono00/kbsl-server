package com.kbsl.server.rank.domain.model;

import com.kbsl.server.boot.domain.model.BaseEntity;
import com.kbsl.server.rank.enums.RankProcessType;
import com.kbsl.server.song.domain.model.Song;
import com.kbsl.server.user.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@Table(name = "tb_rank")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
public class Rank extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    private RankProcessType rankProcessType;

    private Double stars;

    private String description;

    @OneToOne
    @JoinColumn(name = "song_seq")
    private Song song;

    @OneToOne
    @JoinColumn(name = "user_seq")
    private User user;
}
