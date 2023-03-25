package com.kbsl.server.score.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kbsl.server.league.domain.model.League;
import com.kbsl.server.score.domain.model.Score;
import com.kbsl.server.song.domain.model.Song;
import com.kbsl.server.song.dto.response.SongResponseDto;
import com.kbsl.server.user.domain.model.User;
import com.kbsl.server.user.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
public class ScoreResponseDto {

    @Schema(description = "스코어 시퀀스")
    private Long seq;

    @Schema(description = "유저 정보")
    private UserResponseDto user;

    @Schema(description = "노래 정보")
    private SongResponseDto song;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime timePost;

    @Builder
    public ScoreResponseDto(Score entity) {
        this.seq = entity.getSeq();
        this.user = UserResponseDto.builder().entity(entity.getUser()).build();
        this.song = SongResponseDto.builder().entity(entity.getSong()).build();
        this.scoreSeq = entity.getScoreSeq();
        this.baseScore = entity.getBaseScore();
        this.modifiedScore = entity.getModifiedScore();
        this.accuracy = entity.getAccuracy();
        this.badCut = entity.getBadCut();
        this.missedNote = entity.getMissedNote();
        this.bombCut = entity.getBombCut();
        this.wallsHit = entity.getWallsHit();
        this.pause = entity.getPause();
        this.playCount = entity.getPlayCount();
        this.accLeft = entity.getAccLeft();
        this.accRight = entity.getAccRight();
        this.comment = entity.getComment();
        this.timePost = entity.getTimePost();
    }

}
