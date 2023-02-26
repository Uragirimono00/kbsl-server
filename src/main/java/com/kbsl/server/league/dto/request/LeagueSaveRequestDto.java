package com.kbsl.server.league.dto.request;

import com.kbsl.server.league.domain.model.League;
import com.kbsl.server.song.domain.model.Song;
import com.kbsl.server.user.domain.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class LeagueSaveRequestDto {
    @Schema(description = "리그 명")
    private String leagueName;

    @Schema(description = "리그 시작일")
    private LocalDateTime leagueStartDtime;

    @Schema(description = "리그 종료일")
    private LocalDateTime leagueEndDtime;

    @Schema(description = "리그 설명")
    private String description;

    public League toEntity(User userEntity) {
        return League.builder()
                .user(userEntity)
                .leagueName(leagueName)
                .leagueStartDtime(leagueStartDtime)
                .leagueEndDtime(leagueEndDtime)
                .description(description)
                .build();
    }

}
