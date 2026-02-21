package com.app.onestepback.domain.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResultDTO {
    private String impUid;      // 포트원 고유 결제 번호
    private String merchantUid; // 우리가 보낸 주문 번호
    private String status;      // 결제 상태 (paid, failed, cancelled 등)
    private Long amount;        // 결제 금액
    private String failReason;  // 실패 사유 (실패 시에만 존재)
    private String paidAt;      // 결제 승인 시각 (Unix Timestamp or String)

    /**
     * 포트원 API 응답 Map -> DTO 변환 팩토리 메서드
     * - API 응답은 snake_case로 오므로 camelCase로 매핑해준다.
     */
    public static PaymentResultDTO from(Map<String, Object> attributes) {
        if (attributes == null) {
            return null;
        }

        // 금액 처리: 포트원은 Integer나 Double로 줄 수 있으므로 안전하게 변환
        Object amountObj = attributes.get("amount");
        Long amount = 0L;
        if (amountObj instanceof Integer) {
            amount = ((Integer) amountObj).longValue();
        } else if (amountObj instanceof Double) {
            amount = ((Double) amountObj).longValue();
        } else if (amountObj instanceof BigDecimal) {
            amount = ((BigDecimal) amountObj).longValue();
        }

        return PaymentResultDTO.builder()
                .impUid((String) attributes.get("imp_uid"))
                .merchantUid((String) attributes.get("merchant_uid"))
                .status((String) attributes.get("status"))
                .amount(amount)
                .failReason((String) attributes.get("fail_reason"))
                // 필요하다면 paid_at 등 추가 매핑
                .build();
    }
}