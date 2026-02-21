package com.app.onestepback.service.notification;

import com.app.onestepback.domain.model.NotificationVO;

import java.util.List;

/**
 * 사용자 알림 데이터의 영속성(DB) 관리 및 실시간 웹소켓 전송을 담당하는 비즈니스 인터페이스입니다.
 */
public interface NotificationService {

    /**
     * 개별 알림 데이터를 데이터베이스에 저장하고 실시간 소켓 전송을 트리거합니다.
     *
     * @param notificationVO 저장 및 발송 대상 알림 정보
     */
    void send(NotificationVO notificationVO);

    /**
     * 특정 사용자가 아직 확인하지 않은 알림 목록을 조회합니다.
     *
     * @param memberId 조회 대상 사용자의 고유 식별자
     * @return 미확인 알림 데이터 리스트
     */
    List<NotificationVO> getUnreadNotifications(Long memberId);

    /**
     * 특정 알림의 상태를 읽음(확인) 상태로 갱신합니다.
     *
     * @param notificationId 상태를 변경할 대상 알림의 고유 식별자
     */
    void readNotification(Long notificationId);
}