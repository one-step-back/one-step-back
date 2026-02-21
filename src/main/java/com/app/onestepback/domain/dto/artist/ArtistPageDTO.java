package com.app.onestepback.domain.dto.artist;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArtistPageDTO {
    // 1. 아티스트 기본 정보
    private ArtistProfileDTO profile;

    // 2. 통계 정보
    private ArtistStatsDTO stats;

    // 3. 접속자(나)와의 관계
    private ViewerContextDTO myStatus;
}

