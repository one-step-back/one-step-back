package com.app.onestepback.web.api.v1.settlement;

import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.domain.dto.settlement.SettlementDTO;
import com.app.onestepback.global.annotation.LoginUser;
import com.app.onestepback.global.common.Slice;
import com.app.onestepback.global.common.response.ApiResponse;
import com.app.onestepback.service.settlement.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/settlements")
@RequiredArgsConstructor
public class SettlementApiV1 {

    private final SettlementService settlementService;

    /**
     * 아티스트의 수익 정산 대시보드 통계 정보를 조회합니다.
     *
     * @param user 현재 인가된 아티스트의 세션 객체
     * @return 미정산 금액 및 월별 수익 데이터가 포함된 대시보드 DTO
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(
            @LoginUser(artistOnly = true) SessionUser user
    ) {
        SettlementDTO.Dashboard dashboard = settlementService.getSettlementDashboard(user.getId());
        return ResponseEntity.ok(ApiResponse.ok(dashboard));
    }

    /**
     * 누적된 미정산 금액에 대하여 시스템에 정산(지급)을 요청합니다.
     *
     * @param user 현재 인가된 아티스트의 세션 객체
     * @return 처리 결과 메시지가 포함된 표준 응답 객체
     */
    @PostMapping("/request")
    public ResponseEntity<?> requestSettlement(
            @LoginUser(artistOnly = true) SessionUser user
    ) {
        settlementService.requestSettlement(user.getId());
        return ResponseEntity.ok(ApiResponse.ok("정산 신청이 접수되었습니다. 익일 지급됩니다."));
    }

    /**
     * 아티스트의 과거 수익 정산 처리 내역을 무한 스크롤(Slice) 방식으로 조회합니다.
     *
     * @param user   현재 인가된 아티스트의 세션 객체
     * @param lastId 이전 페이지의 마지막 정산 식별자 (커서 기반 페이징)
     * @return 정산 이력 목록 데이터가 포함된 슬라이스(Slice) 응답 객체
     */
    @GetMapping("/history")
    public ResponseEntity<?> getSettlementHistory(
            @LoginUser(artistOnly = true) SessionUser user,
            @RequestParam(value = "lastId", required = false) Long lastId
    ) {
        Slice<SettlementDTO.History> historySlice = settlementService.getSettlementHistory(user.getId(), lastId);
        return ResponseEntity.ok(ApiResponse.ok(historySlice));
    }
}