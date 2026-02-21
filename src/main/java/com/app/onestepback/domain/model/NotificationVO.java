package com.app.onestepback.domain.model;

import com.app.onestepback.domain.type.notification.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class NotificationVO {
    private Long id;
    private Long receiverId; // 받는 사람
    private Long senderId;   // 보낸 사람
    private NotificationType notiType; // 알림 타입 (Enum 처리 권장, DB엔 String으로 들어감)
    private String notiMessage;
    private String notiUrl;
    private boolean read;
    private LocalDateTime createdTime;
}