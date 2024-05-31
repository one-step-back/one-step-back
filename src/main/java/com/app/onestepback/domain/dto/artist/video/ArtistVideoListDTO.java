package com.app.onestepback.domain.dto.artist.video;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ArtistVideoListDTO {
    private Long postId;
    private Long artistId;
    private String title;
    private String videoLink;
    private String videoThumbnail;
    private String content;
    private String category;
    private LocalDateTime writeTime;
    private String timeGap;

    private List<String> tags = new ArrayList<>();

    private String nickname;
    private String kakaoProfileUrl;
    private String profileName;
    private String profilePath;

    private long viewCount;
    private int likeCount;
    private int replyCount;

    private boolean isBookmarked;
}
