package com.kbsl.server.song.domain.model;

import com.kbsl.server.boot.domain.model.BaseEntity;
import com.kbsl.server.song.enums.SongDifficultyType;
import com.kbsl.server.song.enums.SongModeType;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

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

    @Enumerated(EnumType.STRING)
    private SongDifficultyType songDifficulty;

    @Enumerated(EnumType.STRING)
    private SongModeType songModeType;

}
