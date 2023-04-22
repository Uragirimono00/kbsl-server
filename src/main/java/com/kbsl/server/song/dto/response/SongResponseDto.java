package com.kbsl.server.song.dto.response;

import com.kbsl.server.song.domain.model.Song;
import com.kbsl.server.song.domain.model.SongBadge;
import com.kbsl.server.song.domain.model.SongBadgeList;
import com.kbsl.server.song.enums.SongDifficultyType;
import com.kbsl.server.song.enums.SongModeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@NoArgsConstructor
public class SongResponseDto {
    @Schema(description = "노래 시퀀스")
    private Long seq;
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

    @Schema(description = "배지")
    private List<SongBadgeResponseDto> songBadgeResponse;

    @Builder
    public SongResponseDto(Song entity, List<SongBadgeResponseDto> songBadgeList) {
        this.seq = entity.getSeq();
        this.songId = entity.getSongId();
        this.songHash = entity.getSongHash();
        this.songName = entity.getSongName();
        this.songDifficulty = entity.getSongDifficulty();
        this.songModeType = entity.getSongModeType();
        this.uploaderName = entity.getUploaderName();
        this.coverUrl = entity.getCoverUrl();
        this.previewUrl = entity.getPreviewUrl();
        this.downloadUrl = entity.getDownloadUrl();
        if (songBadgeList != null)
            this.songBadgeResponse = songBadgeList;
    }

}
