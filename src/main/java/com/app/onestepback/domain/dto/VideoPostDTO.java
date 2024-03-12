package com.app.onestepback.domain.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class VideoPostDTO {
    //    게시글 번호
    private Long id;
    //    회원 번호
    private Long memberId;
    //    게시글 제목
    private String postTitle;
    //    게시글 부제목
    private String postSubtitle;
    //    게시글 내용
    private String postContent;
    //    글 카테고리
    private String postCategory;
    //    조회수 카운트
    private Long postViewCount;  // 디폴트 - 0
    //    글 작성 시간
    private String postWriteTime;
    //    글 수정 시간
    private String postUpdateTime;
    private String videoLink;

    private String memberNickname;
    private String memberKakaoProfileUrl;
    private String memberProfileName;
    private String memberProfilePath;

    private String tag1;
    private String tag2;
    private String tag3;
    private String tag4;
    private String tag5;
    private int likeCount;
    private int replyCount;
}
