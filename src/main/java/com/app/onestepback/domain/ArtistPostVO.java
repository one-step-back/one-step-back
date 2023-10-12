package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ArtistPostVO {
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
}
