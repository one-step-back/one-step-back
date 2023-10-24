package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class VideoPostVO {
//    게시글 번호
    private Long postId;
//    영상 주소
    private String videoLink;
}
