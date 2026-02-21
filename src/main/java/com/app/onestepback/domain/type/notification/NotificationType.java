package com.app.onestepback.domain.type.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    // 1. 내 글/댓글에 반응
    NEW_COMMENT("내 피드에 댓글이 달렸습니다."),
    NEW_REPLY("내 댓글에 답글이 달렸습니다."),

    // 2. 구독/멤버십 알림
    NEW_FEED("팔로우하는 아티스트가 새 글을 올렸습니다."),
    MEMBERSHIP_FEED("아티스트가 멤버십 전용 글을 올렸습니다."),

    // 3. 결제/갱신 알림
    SUBSCRIPTION_RENEWAL("멤버십 구독이 정상적으로 갱신되었습니다."),
    SUBSCRIPTION_UPDATE("예약된 멤버십 변경이 반영되었습니다."),
    SUBSCRIPTION_FAILED("멤버십 결제에 실패했습니다. 결제 수단을 확인해주세요."),

    // 4. 정산 알림
    SETTLEMENT_COMPLETED("정산 지급 완료"),
    SETTLEMENT_REJECTED("정산 요청 반려"),

    // 5. 크라우드 펀딩 알림
    CROWD_FUNDING_REQUEST("새로운 펀딩 제안이 도착했습니다."),
    CROWD_FUNDING_ACCEPTED("제안하신 펀딩이 승낙되어 오픈되었습니다."),
    CROWD_FUNDING_REJECTED("제안하신 펀딩이 반려되었습니다."),
    CROWD_FUNDING_SUCCESS("참여하신 펀딩이 성공적으로 마감되었습니다."),
    CROWD_FUNDING_FAILED("참여하신 펀딩이 실패하여 환불 처리되었습니다."),
    CROWD_FUNDING_GOAL_REACHED("펀딩 목표 금액 100%를 달성했습니다.");

    private final String defaultMessage;
}