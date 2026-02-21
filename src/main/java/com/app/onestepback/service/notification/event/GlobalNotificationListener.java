package com.app.onestepback.service.notification.event;

import com.app.onestepback.domain.model.NotificationVO;
import com.app.onestepback.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 스프링 이벤트 스트림에 발행된 알림 이벤트를 비동기적으로 수신하여 처리하는 글로벌 리스너 컴포넌트입니다.
 * <p>
 * 메인 비즈니스 로직의 트랜잭션이 성공적으로 커밋된 이후(AFTER_COMMIT)에만 동작하여,
 * 롤백된 데이터에 대한 알림이 잘못 발송되는 데이터 정합성 문제를 원천 차단합니다.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalNotificationListener {

    private final NotificationService notificationService;

    /**
     * 알림 이벤트를 수신하여 데이터베이스에 저장하고 실시간 웹소켓 메시지를 전송합니다.
     *
     * @param event 발행된 알림 이벤트 객체
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotification(NotificationEvent event) {
        try {
            if (event.receiverIds() == null || event.receiverIds().isEmpty()) {
                return;
            }

            for (Long receiverId : event.receiverIds()) {
                if (receiverId.equals(event.senderId())) {
                    continue;
                }

                String message = (event.customMessage() != null)
                        ? event.customMessage()
                        : event.type().getDefaultMessage();

                NotificationVO vo = NotificationVO.builder()
                        .senderId(event.senderId())
                        .receiverId(receiverId)
                        .notiType(event.type())
                        .notiMessage(message)
                        .notiUrl(event.url())
                        .build();

                notificationService.send(vo);
            }
        } catch (Exception e) {
            log.error("[Notification] 비동기 알림 리스너 처리 중 시스템 오류가 발생하였습니다: {}", e.getMessage());
        }
    }
}