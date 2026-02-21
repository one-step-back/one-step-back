package com.app.onestepback.domain.dto.artist;

import com.app.onestepback.global.util.FileUrlSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArtistDetailDTO {
    // [기본 정보]
    private Long artistId;
    private String nickname;
    private String blogName;
    private String description;

    @JsonSerialize(using = FileUrlSerializer.class)
    private String profilePath;
    @JsonSerialize(using = FileUrlSerializer.class)
    private String blogImgPath;

    // [통계 및 상태]
    private Long followerCount;
    private Long subscriptionCount; // 총 구독자 수
    private boolean following;
    private boolean subscribed;
    private Long feedTotal;

    private boolean hasMembership;
    private boolean hasBankInfo;
}