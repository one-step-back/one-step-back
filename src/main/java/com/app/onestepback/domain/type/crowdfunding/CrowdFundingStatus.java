package com.app.onestepback.domain.type.crowdfunding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CrowdFundingStatus {

    // [Phase 1: 대기]
    WAITING("승인 대기"),    // 팬이 요청한 상태 (아티스트 승인 필요)
    REJECTED("반려됨"),      // 아티스트가 제안을 거절함 (End State)

    // [Phase 2: 진행]
    PROCEEDING("진행 중"),   // 아티스트가 승인함. (시작일 전이면 '오픈 예정'으로 화면 처리. 기한 내 후원 가능)

    // [Phase 3: 종료 및 판별 대기]
    ENDED("모금 종료"),      // 기한(endDate) 만료. 추가 후원 불가. 아티스트의 최종 결정(성공/실패)을 기다리는 상태

    // [Phase 4: 최종 결과]
    SUCCESS("펀딩 성공"),    // 아티스트가 진행을 확정함. (이 상태가 되어야만 정산/인출 가능)
    FAILED("펀딩 실패");     // 아티스트가 진행을 포기함. (자동 환불 프로세스 트리거)

    private final String description;
}