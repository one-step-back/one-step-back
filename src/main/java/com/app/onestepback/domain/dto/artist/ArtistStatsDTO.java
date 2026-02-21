package com.app.onestepback.domain.dto.artist;

import lombok.Data;

@Data
public class ArtistStatsDTO {
    private Long followerCount;
    private Long subscriptionCount;
    private Long feedTotal;
}
