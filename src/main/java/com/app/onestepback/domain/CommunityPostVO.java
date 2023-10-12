package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class CommunityPostVO {
    private Long id;
    private Long memberId;
    private String communityPostTitle;
    private String communityPostSubtitle;
    private String communityPostContent;
    private Long communityPostViewCount; //디폴트 = 0
    private String communityPostWriteTime;
    private String communityPostUpdateTime;
}
