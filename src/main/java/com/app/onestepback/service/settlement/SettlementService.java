package com.app.onestepback.service.settlement;

import com.app.onestepback.domain.dto.settlement.SettlementDTO;
import com.app.onestepback.domain.model.SettlementVO;
import com.app.onestepback.global.common.Slice;

import java.util.List;

/**
 * 아티스트의 수익 정산 처리를 담당하는 비즈니스 로직 인터페이스입니다.
 * <p>
 * 대시보드 통계 조회, 정산 요청, 처리 및 실패 시의 롤백 보상 트랜잭션을 관리합니다.
 * </p>
 */
public interface SettlementService {

    /**
     * 아티스트의 미정산 금액, 예상 실수령액 및 월별 매출 추이를 포함한 대시보드 정보를 조회합니다.
     *
     * @param artistId 조회를 요청한 아티스트의 고유 식별자
     * @return 정산 대시보드 요약 및 통계 DTO
     */
    SettlementDTO.Dashboard getSettlementDashboard(Long artistId);

    /**
     * 아티스트의 누적 미정산 금액에 대하여 시스템에 정산 지급을 요청합니다.
     *
     * @param artistId 정산을 요청한 아티스트의 고유 식별자
     */
    void requestSettlement(Long artistId);

    /**
     * 아티스트의 과거 정산 처리 이력을 무한 스크롤(Slice) 방식으로 조회합니다.
     *
     * @param artistId         조회를 요청한 아티스트의 고유 식별자
     * @param lastSettlementId 이전 페이지의 마지막 정산 이력 식별자 (커서 기반 페이징)
     * @return 정산 이력 데이터가 포함된 슬라이스(Slice) 객체
     */
    Slice<SettlementDTO.History> getSettlementHistory(Long artistId, Long lastSettlementId);

    /**
     * 대기 중인 정산 요청에 대해 외부 PG망을 통한 실제 계좌 송금 및 상태 갱신을 수행합니다.
     *
     * @param settlement 처리를 진행할 정산 요청 엔티티
     */
    void processSettlement(SettlementVO settlement);

    /**
     * 송금 실패 등 정산 처리 중 오류가 발생했을 경우, 상태를 반려로 변경하고 관련 결제 내역의 잠금을 해제합니다.
     *
     * @param settlement 처리에 실패한 정산 요청 엔티티
     * @param reason     반려 및 실패 상세 사유
     */
    void handleSettlementFailure(SettlementVO settlement, String reason);

    /**
     * 시스템 내에서 현재 승인 및 송금 대기 중인 모든 정산 요청 목록을 조회합니다.
     *
     * @return 대기 상태(REQUESTED)의 정산 엔티티 리스트
     */
    List<SettlementVO> findRequestedSettlements();
}