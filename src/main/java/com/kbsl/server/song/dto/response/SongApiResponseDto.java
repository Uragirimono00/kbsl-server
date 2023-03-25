package com.kbsl.server.song.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kbsl.server.song.domain.model.Song;
import com.kbsl.server.song.enums.SongDifficultyType;
import com.kbsl.server.song.enums.SongModeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
public class SongApiResponseDto {
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

    @Schema(description = "퍼블리쉬 시간")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime publishedDtime;

    @Builder
    public SongApiResponseDto(Song entity) {
        this.songId = entity.getSongId();
        this.songName = entity.getSongName();
        this.songHash = entity.getSongHash();
        this.songDifficulty = entity.getSongDifficulty();
        this.songModeType = entity.getSongModeType();
        this.uploaderName = entity.getUploaderName();
        this.coverUrl = entity.getCoverUrl();
        this.previewUrl = entity.getPreviewUrl();
        this.downloadUrl = entity.getDownloadUrl();
        this.publishedDtime = entity.getPublishedDtime();
    }
}
