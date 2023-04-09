package com.kbsl.server.song.dto.response;

import com.kbsl.server.song.domain.model.SongBadge;
import com.kbsl.server.song.enums.SongBadgeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class SongBadgeResponseDto {
    @Schema(description = "배지 시퀀스")
    private Long seq;

    @Schema(description = "배지 타입")
    private SongBadgeType songBadgeType;

    @Builder
    public SongBadgeResponseDto(SongBadge entity) {
        this.seq = entity.getSeq();
        this.songBadgeType = entity.getSongBadgeType();
    }
}
