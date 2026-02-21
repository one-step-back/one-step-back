package com.app.onestepback.service.notification;

import com.app.onestepback.domain.dto.notification.NotificationDTO;
import com.app.onestepback.domain.model.NotificationVO;
import com.app.onestepback.repository.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 사용자 알림 처리 로직을 수행하는 서비스 구현체입니다.
 * <p>
 * 알림 저장은 메인 트랜잭션과 무관하게 독립적으로 보장되어야 하므로
 * 물리적인 독립 트랜잭션(REQUIRES_NEW)을 생성하여 데이터를 안전하게 적재합니다.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void send(NotificationVO vo) {
        notificationMapper.insert(vo);

        NotificationDTO response = NotificationDTO.from(vo);

        try {
            messagingTemplate.convertAndSend("/sub/notification/" + vo.getReceiverId(), response);
        } catch (Exception e) {
            log.error("[Notification] 웹소켓 기반 실시간 알림 전송에 실패하였습니다. 대상 식별자: {}, 사유: {}", vo.getReceiverId(), e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationVO> getUnreadNotifications(Long memberId) {
        return notificationMapper.selectUnreadNotifications(memberId);
    }

    @Override
    @Transactional
    public void readNotification(Long notificationId) {
        notificationMapper.updateIsRead(notificationId);
    }
}