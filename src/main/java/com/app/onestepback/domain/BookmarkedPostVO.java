package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class BookmarkedPostVO {
//    게시글 번호
    private Long postId;
//    회원 번호
    private Long memberId;
}
