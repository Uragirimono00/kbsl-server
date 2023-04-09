package com.kbsl.server.song.dto.request;

import com.kbsl.server.song.domain.model.SongBadge;
import com.kbsl.server.song.enums.SongBadgeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class SongBadgeSaveRequestDto {
    @Schema(description = "배지타입")
    private SongBadgeType songBadgeType;

    public SongBadge toEntity() {
        return SongBadge.builder()
                .songBadgeType(songBadgeType)
                .build();
    }
}
