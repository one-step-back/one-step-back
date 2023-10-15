package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class VideoDTO {
    private Long id;
    private Long memberId;
    private String videoTitle;
    private String videoContent;
    private String videoLink;
    private Long videoViewCount; //디폴트 = 0
    private String videoWriteTime;
    private String videoUpdateTime;

    private String memberNickname;
    private String memberKakaoProfileUrl;
    private String memberImgName;
    private String memberImgPath;

    private String tag1;
    private String tag2;
    private String tag3;
    private String tag4;
    private String tag5;
    private int likeCount;
    private int replyCount;
}
