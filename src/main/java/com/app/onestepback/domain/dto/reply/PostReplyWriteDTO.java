package com.app.onestepback.domain.dto.reply;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostReplyWriteDTO {
    private Long postId;
    private Long memberId;
    private String content;
}
