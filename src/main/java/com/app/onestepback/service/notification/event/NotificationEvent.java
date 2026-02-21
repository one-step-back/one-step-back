package com.app.onestepback.service.notification.event;

import com.app.onestepback.domain.type.notification.NotificationType;

import java.util.List;

/**
 * 애플리케이션 내부에서 알림 발송을 요청할 때 사용되는 이벤트 레코드(DTO)입니다.
 * <p>
 * 스프링의 이벤트 퍼블리싱 메커니즘을 통해 리스너에게 전달되며,
 * 단일 발송 및 다중(브로드캐스트) 발송을 모두 지원하도록 설계되었습니다.
 * </p>
 *
 * @param senderId      알림을 발송하는 주체의 식별자 (시스템 알림일 경우 null)
 * @param receiverIds   알림을 수신할 대상 식별자 목록
 * @param type          발송할 알림의 비즈니스 유형
 * @param url           사용자가 알림 클릭 시 이동할 대상 URL 경로
 * @param customMessage 기본 메시지를 대체할 사용자 정의 메시지 (선택 사항)
 */
public record NotificationEvent(
        Long senderId,
        List<Long> receiverIds,
        NotificationType type,
        String url,
        String customMessage
) {
    /**
     * 단일 수신자를 대상으로 하는 기본 알림 이벤트를 생성합니다.
     */
    public static NotificationEvent single(Long senderId, Long receiverId, NotificationType type, String url) {
        return new NotificationEvent(senderId, List.of(receiverId), type, url, null);
    }

    /**
     * 단일 수신자를 대상으로 사용자 정의 메시지가 포함된 알림 이벤트를 생성합니다.
     */
    public static NotificationEvent single(Long senderId, Long receiverId, NotificationType type, String url, String customMessage) {
        return new NotificationEvent(senderId, List.of(receiverId), type, url, customMessage);
    }

    /**
     * 다수의 수신자를 대상으로 하는 브로드캐스트 알림 이벤트를 생성합니다.
     */
    public static NotificationEvent broadcast(Long senderId, List<Long> receiverIds, NotificationType type, String url) {
        return new NotificationEvent(senderId, receiverIds, type, url, null);
    }
}