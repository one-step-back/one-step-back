package com.app.onestepback.domain.dto.artist;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArtistDetailDTO {
    private Long artistId;
    private String nickname;
    private String kakaoProfileUrl;
    private String profileName;
    private String profilePath;
    private String blogName;
    private String description;
    private String blogImgName;
    private String blogImgPath;
    private Long subscriptionCount;
    private Long postCount;
}
