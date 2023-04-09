package com.kbsl.server.song.domain.model;

import com.kbsl.server.boot.domain.model.BaseEntity;
import com.kbsl.server.league.domain.model.League;
import com.kbsl.server.song.enums.SongDifficultyType;
import com.kbsl.server.song.enums.SongModeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Entity
@Table(name = "tb_song")
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
public class Song extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    private String songId;

    private String songHash;

    private String songName;

    @Enumerated(EnumType.STRING)
    private SongDifficultyType songDifficulty;

    @Enumerated(EnumType.STRING)
    private SongModeType songModeType;

    private String uploaderName;

    private String coverUrl;

    private String previewUrl;

    private String downloadUrl;

    private LocalDateTime publishedDtime;

    @OneToMany(mappedBy = "song")
    private List<SongBadgeList> songBadgeList;
}
