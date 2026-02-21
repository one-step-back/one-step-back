package com.app.onestepback.service.bank;

import com.app.onestepback.domain.type.bank.BankCode;
import com.app.onestepback.global.client.PortOneClient;
import com.app.onestepback.global.config.PortOneConfig;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * 포트원(PortOne) V1 API를 연동하여 실계좌 예금주 성명 일치 여부를 검증하는 구현체입니다.
 * <p>
 * 기존의 중복된 토큰 발급 로직을 제거하고, 전역 통신 클라이언트인 {@link PortOneClient}를 재사용하여
 * 시스템의 응집도를 높이고 유지보수성을 극대화하였습니다.
 * </p>
 */
@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class PortOneV1BankVerificationService implements BankVerificationService {

    private final RestTemplate restTemplate;
    private final PortOneClient portOneClient;
    private final PortOneConfig portOneConfig;

    /* 외부 통신 응답의 타입 안정성을 보장하기 위한 내부 전용 레코드 */
    private record VbankHolderResponse(Integer code, String message, HolderData response) {
        public record HolderData(@JsonProperty("bank_holder") String bankHolder) {}
    }

    @Override
    public boolean verifyAccountHolder(String bankName, String accountNumber, String accountHolder) {
        try {
            String accessToken = portOneClient.getAccessToken();
            if (accessToken == null) {
                return false;
            }

            BankCode bankCode = BankCode.from(bankName);
            String url = String.format("%s/vbanks/holder?bank_code=%s&bank_num=%s",
                    portOneConfig.getApiUrl(), bankCode.getCode(), accountNumber);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<VbankHolderResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, VbankHolderResponse.class
            );

            VbankHolderResponse body = response.getBody();

            if (body != null && body.code() != null && body.code() == 0 && body.response() != null) {
                String fetchedHolder = body.response().bankHolder();
                log.info("[Bank Verification] 조회 완료. 반환된 예금주: {}, 요청된 예금주: {}", fetchedHolder, accountHolder);

                return fetchedHolder.replace(" ", "").equals(accountHolder.replace(" ", ""));
            } else {
                String errorMsg = (body != null && body.message() != null) ? body.message() : "응답 데이터가 존재하지 않습니다.";
                log.warn("[Bank Verification] 예금주 검증 실패: {}", errorMsg);
            }

        } catch (HttpClientErrorException e) {
            log.error("[Bank Verification] 외부 API 연동 오류. 상태 코드: {}, 응답 본문: {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("[Bank Verification] 시스템 처리 중 예기치 않은 오류 발생", e);
        }
        return false;
    }
}