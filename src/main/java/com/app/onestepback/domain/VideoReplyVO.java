package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class VideoReplyVO {
    private Long id;
    private Long videoId;
    private Long memberId;
    private String replyContent;
}
