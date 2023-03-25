package com.kbsl.server.league.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kbsl.server.league.domain.model.League;
import com.kbsl.server.song.domain.model.Song;
import com.kbsl.server.song.dto.response.SongResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@NoArgsConstructor
public class LeagueDeatilResponseDto {
    @Schema(description = "리그 시퀀스")
    private Long seq;

    @Schema(description = "유저 시퀀스")
    private Long userSeq;

    @Schema(description = "유저 이름")
    private String userName;

    @Schema(description = "프로필사진")
    private String imageUrl;

    @Schema(description = "리그 명")
    private String leagueName;

    @Schema(description = "리그 시작일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime leagueStartDtime;

    @Schema(description = "리그 종료일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime leagueEndDtime;

    @Schema(description = "리그 설명")
    private String description;

    @Schema(description = "노래 리스트")
    private List<SongResponseDto> songsList;

    @Schema(description = "현재 리그 상태")
    private String leagueStatus;

    public void setSongsList(List<SongResponseDto> songsList) {
        this.songsList = songsList;
    }

    @Builder
    public LeagueDeatilResponseDto(League entity, String leagueStatus) {
        this.seq = entity.getSeq();
        this.userSeq = entity.getUser().getSeq();
        this.userName = entity.getUser().getUsername();
        this.imageUrl = entity.getUser().getImageUrl();
        this.leagueName = entity.getLeagueName();
        this.leagueStartDtime = entity.getLeagueStartDtime();
        this.leagueEndDtime = entity.getLeagueEndDtime();
        this.description = entity.getDescription();
        this.songsList = entity.getSongsList().stream()
                .map(SongResponseDto::new)
                .collect(Collectors.toList());
        this.leagueStatus = leagueStatus;
    }

}
