package com.kbsl.server.song.dto.response;

import com.kbsl.server.song.domain.model.Song;
import com.kbsl.server.song.enums.SongDifficultyType;
import com.kbsl.server.song.enums.SongModeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class SongResponseDto {
    @Schema(description = "노래 ID")
    private String songId;

    @Schema(description = "노래 해쉬")
    private String songHash;

    @Schema(description = "노래 난이도", example = "ExpertPlus")
    private SongDifficultyType songDifficulty;

    @Schema(description = "노래 모드", example = "Standard")
    private SongModeType songModeType;

    @Builder
    public SongResponseDto(Song entity) {
        this.songId = entity.getSongId();
        this.songHash = entity.getSongHash();
        this.songDifficulty = entity.getSongDifficulty();
        this.songModeType = entity.getSongModeType();
    }

}
