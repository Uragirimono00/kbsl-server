package com.kbsl.server.league.domain.model;

import com.kbsl.server.boot.domain.model.BaseEntity;
import com.kbsl.server.song.domain.model.Song;
import com.kbsl.server.user.domain.model.User;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Entity
@Table(name = "tb_league")
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
public class League extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userSeq")
    private User user;

    private String leagueName;

    private LocalDateTime leagueStartDtime;

    private LocalDateTime leagueEndDtime;

    private String description;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "tb_league_song",
            joinColumns = @JoinColumn(name = "leagueSeq"),
            inverseJoinColumns = @JoinColumn(name = "songSeq")
    )
    private List<Song> songsList;

    public void setSongsList(List<Song> songsList) {
        this.songsList = songsList;
    }
}
