package com.kbsl.server.score.dto.request;

import com.kbsl.server.league.domain.model.League;
import com.kbsl.server.score.domain.model.Score;
import com.kbsl.server.song.domain.model.Song;
import com.kbsl.server.song.dto.response.SongResponseDto;
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

    @Schema(description = "비트리더 스코어 시퀀스")
    private Long steamId;

    @JsonIgnore
    @Schema(description = "비트리더 스코어 시퀀스")
    private Long scoreSeq;

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

    @Schema(description = "코멘트")
    private String comment;

    @Schema(description = "기록된 시간 유닉스 타임")
    private LocalDateTime timePost;

    public Score toEntity(User userEntity, Song songEntity) {
        return Score.builder()
                .user(userEntity)
                .song(songEntity)
                .scoreSeq(scoreSeq)
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
                .comment(comment)
                .timePost(timePost)
                .build();
    }
}
