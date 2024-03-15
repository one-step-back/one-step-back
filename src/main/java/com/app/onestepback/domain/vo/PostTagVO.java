package com.app.onestepback.domain.vo;

import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
public class PostTagVO {
//    게시글 번호
    private Long postId;
//    태그 이름
    private String postTagName;

    @Builder
    public PostTagVO(Long postId, String postTagName) {
        this.postId = postId;
        this.postTagName = postTagName;
    }
}
