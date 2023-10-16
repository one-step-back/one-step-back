package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ArtistPostDTO {
    private Long id;
    private Long memberId;
    private String postTitle;
    private String postSubtitle;
    private String postContent;
    private Long postViewCount; //디폴트 = 0
    private String postImgName;
    private String postImgPath;
    private String postWriteTime;
    private String postUpdateTime;

    private String memberNickname;
    private String memberKakaoProfileUrl;
    private String memberProfileName;
    private String memberProfilePath;

    private String tag1;
    private String tag2;
    private String tag3;
    private String tag4;
    private String tag5;
    private int likeCount;
}
