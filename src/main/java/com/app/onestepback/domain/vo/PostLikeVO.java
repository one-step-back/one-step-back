package com.app.onestepback.domain.vo;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.lang.management.LockInfo;

@Component
@Data
public class PostLikeVO {
//    게시글 번호
    private Long postId;
//    회원 번호
    private Long memberId;
}
