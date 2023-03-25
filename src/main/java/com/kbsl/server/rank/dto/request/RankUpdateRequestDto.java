package com.kbsl.server.rank.dto.request;

import com.kbsl.server.league.domain.model.League;
import com.kbsl.server.rank.domain.model.Rank;
import com.kbsl.server.rank.enums.RankProcessType;
import com.kbsl.server.song.dto.response.SongResponseDto;
import com.kbsl.server.user.domain.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
public class RankUpdateRequestDto {

    @Schema(description = "랭크 진행 상태")
    private RankProcessType rankProcessType;

    @Schema(description = "난이도")
    private Double stars;

    @Schema(description = "설명")
    private String description;

    public Rank toEntity(User userEntity) {
        return Rank.builder()
            .user(userEntity)
            .rankProcessType(rankProcessType)
            .stars(stars)
            .description(description)
            .build();
    }

}
