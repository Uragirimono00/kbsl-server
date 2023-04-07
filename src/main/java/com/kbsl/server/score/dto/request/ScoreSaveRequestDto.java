package com.kbsl.server.score.dto.request;

import com.kbsl.server.league.domain.model.League;
import com.kbsl.server.score.domain.model.Score;
import com.kbsl.server.song.domain.model.Song;
import com.kbsl.server.song.dto.response.SongResponseDto;
import com.kbsl.server.song.enums.SongDifficultyType;
import com.kbsl.server.song.enums.SongModeType;
import com.kbsl.server.user.domain.model.User;
import com.kbsl.server.user.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minidev.json.annotate.JsonIgnore;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
public class ScoreSaveRequestDto {

    @Schema(description = "기본 점수")
    private Long baseScore;

    @Schema(description = "모드 점수")
    private Long modifiedScore;

    @Schema(description = "정확도")
    private Double accuracy;

    @Schema(description = "배드컷")
    private Integer badCut;

    @Schema(description = "미스 노트")
    private Integer missedNote;

    @Schema(description = "폭탄 히트")
    private Integer bombCut;

    @Schema(description = "벽 닿은 횟수")
    private Integer wallsHit;

    @Schema(description = "퍼즈")
    private Integer pause;

    @Schema(description = "플레이 카운트")
    private Integer playCount;

    @Schema(description = "왼손 평균 점수")
    private Double accLeft;

    @Schema(description = "오른손 평균 점수")
    private Double accRight;

    @Schema(description = "노래 해쉬")
    private String songHash;

    @Schema(description = "노래 난이도", example = "ExpertPlus")
    private SongDifficultyType songDifficulty;

    @Schema(description = "노래 모드", example = "Standard")
    private SongModeType songModeType;

    public Score toEntity(User userEntity, Song songEntity) {
        return Score.builder()
                .user(userEntity)
                .song(songEntity)
                .baseScore(baseScore)
                .modifiedScore(modifiedScore)
                .accuracy(accuracy)
                .badCut(badCut)
                .missedNote(missedNote)
                .bombCut(bombCut)
                .wallsHit(wallsHit)
                .pause(pause)
                .playCount(playCount)
                .accLeft(accLeft)
                .accRight(accRight)
                .build();
    }
}
