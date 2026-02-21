package com.app.onestepback.service.notification.event;

import com.app.onestepback.domain.type.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 비즈니스 로직에서 발생하는 알림 요청을 스프링 이벤트 스트림으로 발행하는 퍼블리셔 컴포넌트입니다.
 * <p>
 * 호출부(Service)에서 알림 발송 실패로 인해 메인 트랜잭션이 롤백되거나
 * 불필요한 예외 처리 코드가 양산되는 것을 방지하기 위해, 내부적으로 예외를 캡슐화하여 로깅만 수행합니다.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationPublisher {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * 다수의 수신자에게 동일한 알림 이벤트를 발행합니다.
     *
     * @param senderId    발신자 식별자
     * @param receiverIds 수신자 식별자 목록
     * @param type        알림 유형
     * @param url         이동할 URL 경로
     */
    public void sendBroadcast(Long senderId, List<Long> receiverIds, NotificationType type, String url) {
        if (receiverIds == null || receiverIds.isEmpty()) return;

        try {
            eventPublisher.publishEvent(NotificationEvent.broadcast(senderId, receiverIds, type, url));
        } catch (Exception e) {
            log.error("[Notification] 단체 알림 이벤트 발행 중 시스템 오류가 발생하였습니다. Type: {}, Error: {}", type, e.getMessage());
        }
    }

    /**
     * 단일 수신자에게 기본 메시지 형태의 알림 이벤트를 발행합니다.
     *
     * @param senderId   발신자 식별자
     * @param receiverId 수신자 식별자
     * @param type       알림 유형
     * @param url        이동할 URL 경로
     */
    public void send(Long senderId, Long receiverId, NotificationType type, String url) {
        try {
            eventPublisher.publishEvent(NotificationEvent.single(senderId, receiverId, type, url));
        } catch (Exception e) {
            log.error("[Notification] 단일 알림 이벤트 발행 중 시스템 오류가 발생하였습니다. Type: {}, Receiver: {}, Error: {}", type, receiverId, e.getMessage());
        }
    }

    /**
     * 단일 수신자에게 사용자 정의 메시지가 포함된 알림 이벤트를 발행합니다.
     *
     * @param senderId   발신자 식별자
     * @param receiverId 수신자 식별자
     * @param type       알림 유형
     * @param url        이동할 URL 경로
     * @param content    사용자 정의 메시지 내용
     */
    public void send(Long senderId, Long receiverId, NotificationType type, String url, String content) {
        try {
            eventPublisher.publishEvent(NotificationEvent.single(senderId, receiverId, type, url, content));
        } catch (Exception e) {
            log.error("[Notification] 커스텀 단일 알림 이벤트 발행 중 오류가 발생하였습니다. Type: {}, Receiver: {}, Error: {}", type, receiverId, e.getMessage());
        }
    }
}