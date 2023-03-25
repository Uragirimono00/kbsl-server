package com.kbsl.server.rank.dto.response;

import com.kbsl.server.rank.domain.model.Rank;
import com.kbsl.server.rank.enums.RankProcessType;
import com.kbsl.server.song.domain.model.Song;
import com.kbsl.server.song.dto.response.SongResponseDto;
import com.kbsl.server.user.domain.model.User;
import com.kbsl.server.user.dto.response.UserDetailResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor
public class RankResponseDto {

    @Schema(description = "랭크 시퀀스")
    private Long seq;

    @Schema(description = "랭크 진행 상태")
    private RankProcessType rankProcessType;

    @Schema(description = "난이도")
    private Double stars;

    @Schema(description = "설명")
    private String description;

    @Schema(description = "노래")
    private SongResponseDto song;

    @Schema(description = "랭크 지명자")
    private UserDetailResponseDto user;

    @Builder
    public RankResponseDto(Rank entity) {
        this.seq = entity.getSeq();
        this.rankProcessType = entity.getRankProcessType();
        this.stars = entity.getStars();
        this.description = entity.getDescription();
        this.song = SongResponseDto.builder().entity(entity.getSong()).build();
        this.user = UserDetailResponseDto.builder().entity(entity.getUser()).build();
    }
}
