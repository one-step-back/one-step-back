package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class BookmarkedArtistPostVO {
    private Long postId;
    private Long memberId;
    private String bookmarkAddedTime;
}
