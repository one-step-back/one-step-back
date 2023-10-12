package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class BookmarkedCommunityPostVO {
    private Long id;
    private Long communityPostId;
    private Long memberId;
}
