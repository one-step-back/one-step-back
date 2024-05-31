package com.app.onestepback.domain.dto.artist.video;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ArtistVideoDetailDTO {
    private Long postId;
    private Long artistId;
    private String title;
    private String videoLink;
    private String embedLink;
    private String content;
    private String category;
    private LocalDateTime writeTime;

    private String nickname;
    private String kakaoProfileUrl;
    private String profileName;
    private String profilePath;

    private String blogName;
    private long subscriptionCount;

    private long viewCount;
    private int likeCount;
    private int replyCount;

    private boolean isSubscribed;
    private boolean isLiked;
    private boolean isBookmarked;

    private Long previousPostId;
    private String previousPostTitle;
    private Long nextPostId;
    private String nextPostTitle;

    List<String> tags = new ArrayList<>();
}
