package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class NotificationVO {
//    알림 id
    private Long id;
//    게시글 번호
    private Long postId;
//    회원 번호
    private Long memberId;
//    알림 확인
    private String notificationCheck; // (디폴트)안읽으면 UNREAD /읽으면 READ
}
