package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class PostReplyVO {
//    리플라이 id
    private Long id;
//    게시글 번호
    private Long postId;
//    회원 번호
    private Long memberId;
//    댓글 내용
    private String replyContent;
//    댓글 작성 시간
    private String replyWriteTime;
//    댓글 수정 시간
    private String replyUpdateTime;
}
