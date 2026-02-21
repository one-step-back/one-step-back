package com.app.onestepback.web.api.v1.notification;

import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.domain.model.NotificationVO;
import com.app.onestepback.global.annotation.LoginUser;
import com.app.onestepback.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationApiV1 {

    private final NotificationService notificationService;

    /**
     * 현재 로그인한 사용자의 읽지 않은 시스템 알림 목록을 조회합니다.
     *
     * @param user 현재 인가된 사용자의 세션 객체
     * @return 읽지 않은 알림 엔티티 리스트
     */
    @GetMapping("/unread")
    public List<NotificationVO> getUnreadNotifications(@LoginUser SessionUser user) {
        return notificationService.getUnreadNotifications(user.getId());
    }

    /**
     * 특정 시스템 알림의 상태를 '읽음(Read)'으로 처리합니다.
     *
     * @param id 읽음 처리할 알림의 고유 식별자
     */
    @PatchMapping("/{id}/read")
    public void readNotification(@PathVariable("id") Long id) {
        notificationService.readNotification(id);
    }
}