package com.app.onestepback.global.util;

import com.app.onestepback.domain.dto.portone.PortOneBillingKeyResponse;
import com.app.onestepback.global.client.PortOneClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

/**
 * 외부 결제 시스템(PortOne)에 등록된 빌링키 및 결제 수단의
 * 비즈니스 유효성을 검증하는 전담 컴포넌트입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentValidator {
    private final PortOneClient portOneClient;

    /**
     * 식별된 고객의 빌링키 유효성을 외부 시스템과 통신하여 검증합니다.
     *
     * @param billingKey 검증을 수행할 고객의 빌링키 고유 식별자
     * @return 외부 시스템에서 반환된 결제 수단 상세 정보
     * @throws IllegalStateException 시스템 설정 또는 인증 정보가 올바르지 않은 경우 발생
     * @throws IllegalArgumentException 유효하지 않거나 폐기된 결제 수단일 경우 발생
     */
    public PortOneBillingKeyResponse validateRegistration(String billingKey) {
        try {
            return portOneClient.getCustomerInfo(billingKey);
        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("[Payment Validation] 외부 시스템 인증 실패. API Key 및 Secret 설정을 확인하십시오.");
            throw new IllegalStateException("결제 연동 시스템 접근 권한이 유효하지 않습니다. 관리자에게 문의하십시오.");
        } catch (HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException("시스템에 등록되지 않았거나 유효하지 않은 결제 수단입니다.");
        } catch (Exception e) {
            log.error("[Payment Validation] 결제 수단 검증 중 예기치 않은 오류 발생: {}", e.getMessage());
            throw new IllegalArgumentException("결제 수단 정보를 확인하는 중 오류가 발생하였습니다.");
        }
    }
}