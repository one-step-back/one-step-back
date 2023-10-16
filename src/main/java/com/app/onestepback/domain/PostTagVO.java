package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class PostTagVO {
//    게시글 번호
    private Long postId;
//    태그 이름
    private String postTagName;
}
