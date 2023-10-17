package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class NoticeVO {
//    공지사항 id
    private Long id;
//    회원 번호
    private Long memberId;
//    공지 제목
    private String noticeTitle;
//    공지 내용
    private String noticeContent;
//    공지 작성 시간
    private String noticeWriteTime;
}
