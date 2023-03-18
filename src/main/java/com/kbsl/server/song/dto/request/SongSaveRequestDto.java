package com.kbsl.server.song.dto.request;

import com.kbsl.server.league.domain.model.League;
import com.kbsl.server.song.domain.model.Song;
import com.kbsl.server.song.enums.SongDifficultyType;
import com.kbsl.server.song.enums.SongModeType;
import com.kbsl.server.user.domain.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
public class SongSaveRequestDto {
    @Schema(description = "노래 ID")
    private String songId;

    @Schema(description = "노래 해쉬")
    private String songHash;

    @Schema(description = "노래 제목")
    private String songName;
    @Schema(description = "노래 난이도", example = "ExpertPlus")
    private SongDifficultyType songDifficulty;

    @Schema(description = "노래 모드", example = "Standard")
    private SongModeType songModeType;

    @Schema(description = "업로더 이름")
    private String uploaderName;

    @Schema(description = "커버 사진")
    private String coverUrl;

    @Schema(description = "노래")
    private String previewUrl;

    @Schema(description = "다운로드 경로")
    private String downloadUrl;

    public Song toEntity() {
        return Song.builder()
                .songId(songId)
                .songName(songName)
                .songHash(songHash)
                .songDifficulty(songDifficulty)
                .songModeType(songModeType)
                .uploaderName(uploaderName)
                .coverUrl(coverUrl)
                .previewUrl(previewUrl)
                .downloadUrl(downloadUrl)
                .build();
    }
}
