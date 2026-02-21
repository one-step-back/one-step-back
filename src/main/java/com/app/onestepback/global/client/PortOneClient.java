package com.app.onestepback.global.client;

import com.app.onestepback.domain.dto.payment.PaymentResultDTO;
import com.app.onestepback.domain.dto.portone.PortOneBillingKeyResponse;
import com.app.onestepback.domain.type.bank.BankCode;
import com.app.onestepback.global.config.PortOneConfig;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 포트원(PortOne) 외부 API 시스템과의 통신을 전담하는 클라이언트 컴포넌트입니다.
 * <p>
 * 결제 토큰 발급, 빌링키 관리, 자동 결제 요청, 결제 취소 및 펌뱅킹(모의) 기능을 수행합니다.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PortOneClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final PortOneConfig config;

    private record PortOneTokenResponse(Integer code, String message, TokenData response) {
        public record TokenData(@JsonProperty("access_token") String accessToken) {}
    }

    private record PortOneGenericResponse(Integer code, String message) {}

    private record PortOnePaymentResponse(Integer code, String message, Map<String, Object> response) {}


    /**
     * 포트원 API 통신에 필요한 인증 토큰(Access Token)을 발급받습니다.
     *
     * @return 발급된 Access Token 문자열
     * @throws RuntimeException 토큰 발급 요청 실패 시 발생
     */
    public String getAccessToken() {
        String url = "https://api.iamport.kr/users/getToken";
        Map<String, String> body = Map.of(
                "imp_key", config.getApiKey(),
                "imp_secret", config.getApiSecret()
        );

        try {
            ResponseEntity<PortOneTokenResponse> response = restTemplate.postForEntity(url, body, PortOneTokenResponse.class);
            PortOneTokenResponse bodyData = response.getBody();

            if (bodyData == null || bodyData.response() == null || (bodyData.code() != null && bodyData.code() != 0)) {
                String errorMsg = bodyData != null ? bodyData.message() : "응답 값이 비어있습니다.";
                throw new IllegalStateException("포트원 토큰 발급 거절: " + errorMsg);
            }

            return bodyData.response().accessToken();

        } catch (Exception e) {
            log.error("[PortOne API] 인증 토큰 발급에 실패하였습니다. 사용된 API Key: {}", config.getApiKey());
            throw new RuntimeException("토큰 발급 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 고객 식별 번호(Customer UID)를 기반으로 등록된 빌링키(Billing Key) 정보를 조회합니다.
     *
     * @param billingKey 조회할 빌링키 (Customer UID)
     * @return 조회된 결제 수단 정보 응답 객체 (조회 실패 시 null 반환)
     */
    public PortOneBillingKeyResponse getCustomerInfo(String billingKey) {
        String url = "https://api.iamport.kr/subscribe/customers/" + billingKey;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", getAccessToken());

        try {
            ResponseEntity<PortOneBillingKeyResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    PortOneBillingKeyResponse.class
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("[PortOne API] 빌링키 정보 조회 실패. 대상 빌링키: {}, 예외 메시지: {}", billingKey, e.getMessage());
            return null;
        }
    }

    /**
     * 포트원 서버에 등록된 빌링키(Billing Key)의 삭제를 요청합니다.
     *
     * @param billingKey 삭제할 빌링키 (Customer UID)
     * @throws IllegalStateException 외부 결제 시스템과의 통신 오류 또는 삭제 실패 시 발생
     */
    public void deleteBillingKey(String billingKey) {
        String url = "https://api.iamport.kr/subscribe/customers/" + billingKey;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", getAccessToken());

        try {
            restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), PortOneGenericResponse.class);
        } catch (Exception e) {
            log.error("[PortOne API] 빌링키 삭제 요청 실패. 대상 빌링키: {}", billingKey);
            throw new IllegalStateException("외부 결제 연동 시스템에서 결제 수단 삭제 처리에 실패하였습니다.");
        }
    }

    /**
     * 사전에 등록된 빌링키(Customer UID)를 이용하여 결제 승인을 요청합니다.
     *
     * @param customerUid 결제에 사용할 고객 식별 번호 (빌링키)
     * @param merchantUid 가맹점에서 생성한 고유 주문 번호
     * @param amount      결제 요청 금액
     * @param name        결제 상품 또는 서비스 명칭
     * @return 결제 승인 결과가 포함된 응답 객체
     * @throws IllegalStateException 결제 승인 거절 또는 통신 오류 발생 시
     */
    public PaymentResultDTO payWithBillingKey(String customerUid, String merchantUid, Long amount, String name) {
        String url = "https://api.iamport.kr/subscribe/payments/again";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", getAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("customer_uid", customerUid);
        body.put("merchant_uid", merchantUid);
        body.put("amount", amount);
        body.put("name", name);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<PortOnePaymentResponse> response = restTemplate.postForEntity(url, request, PortOnePaymentResponse.class);
            PortOnePaymentResponse bodyData = response.getBody();

            if (bodyData != null && bodyData.code() != null && bodyData.code() == 0) {
                return PaymentResultDTO.from(bodyData.response());
            } else {
                String errorMessage = bodyData != null ? bodyData.message() : "응답 값이 비어있습니다.";
                throw new IllegalStateException("결제 승인 거절: " + errorMessage);
            }
        } catch (Exception e) {
            throw new IllegalStateException("포트원 결제 처리 연동 중 오류가 발생하였습니다: " + e.getMessage());
        }
    }

    /**
     * 기 승인된 결제 건에 대하여 취소(환불)를 요청합니다.
     *
     * @param impUid   포트원 시스템에서 채번된 결제 고유 번호
     * @param reason   결제 취소 사유
     * @param checksum 취소 요청 금액의 무결성 검증을 위한 체크섬
     * @throws RuntimeException 결제 취소 통신 오류 또는 실패 시 발생 (수동 환불 필요 상태)
     */
    public void cancelPayment(String impUid, String reason, Long checksum) {
        String url = "https://api.iamport.kr/payments/cancel";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", getAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("imp_uid", impUid);
        body.put("reason", reason);
        body.put("checksum", checksum);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<PortOneGenericResponse> response = restTemplate.postForEntity(url, request, PortOneGenericResponse.class);
            PortOneGenericResponse bodyData = response.getBody();

            /* HTTP 200 상태 코드로 응답이 오더라도 내부 code 값이 0이 아닐 경우 논리적 실패로 간주합니다. */
            if (bodyData != null && bodyData.code() != null && bodyData.code() != 0) {
                String errorMsg = bodyData.message();
                throw new IllegalStateException("결제 취소 승인 거절: " + errorMsg);
            }

            log.info("[PortOne API] 결제 취소 처리가 완료되었습니다. 고유번호(impUid): {}, 사유: {}", impUid, reason);

        } catch (Exception e) {
            log.error("[PortOne API] 결제 취소 요청이 실패하였습니다. 관리자의 수동 환불 처리가 필요합니다. 고유번호(impUid): {}, 원인: {}", impUid, e.getMessage());
            throw new RuntimeException("결제 취소 연동 중 심각한 오류가 발생하였습니다.", e);
        }
    }

    /**
     * 정산 대금을 수취인의 계좌로 입금하는 펌뱅킹(Firmbanking) 송금 기능의 모의(Mock) 메서드입니다.
     * <p>
     * 실제 은행망 연동 계약이 이루어지기 전까지 운영 시스템에서 송금 프로세스를 대체하기 위해 사용됩니다.
     * </p>
     *
     * @param bankCode      수취 은행 식별 코드
     * @param accountNumber 수취 계좌 번호
     * @param holderName    예금주 명
     * @param amount        송금 요청 금액
     * @throws IllegalStateException 송금 프로세스 중단 또는 예외 발생 시
     */
    public void transferMoney(BankCode bankCode, String accountNumber, String holderName, Long amount) {
        log.info("=========================================================");
        log.info("[System Mock] 펌뱅킹 모의 송금 프로세스를 시작합니다.");
        log.info("---------------------------------------------------------");
        log.info("  수취 은행 : {} (BankCode: {})", bankCode.getDescription(), bankCode.getCode());
        log.info("  계좌 번호 : {}", accountNumber);
        log.info("  예금주 명 : {}", holderName);
        log.info("  송금 금액 : {}원", amount);
        log.info("=========================================================");

        try {
            /* 실제 은행 전용선 통신 환경과 유사한 환경을 조성하기 위해 의도적인 지연을 발생시킵니다. */
            Thread.sleep(500);

            log.info("[System Mock] 펌뱅킹 모의 송금 처리가 정상적으로 완료되었습니다. (Transaction ID: mock_tid_{})", System.currentTimeMillis());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("시스템 스레드 인터럽트로 인해 송금 프로세스가 중단되었습니다.");
        } catch (Exception e) {
            log.error("[System Mock] 펌뱅킹 송금 모의 처리 중 시스템 장애가 발생하였습니다: {}", e.getMessage());
            throw new IllegalStateException("송금 처리 중 시스템 오류가 발생하였습니다: " + e.getMessage());
        }
    }
}