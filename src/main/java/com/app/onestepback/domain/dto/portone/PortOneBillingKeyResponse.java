package com.app.onestepback.domain.dto.portone;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class PortOneBillingKeyResponse {

    private Integer code;
    private String message;
    private BillingKeyDetail response;

    @Getter
    @ToString
    @NoArgsConstructor
    public static class BillingKeyDetail {
        @JsonProperty("card_code")
        private String cardCode;

        @JsonProperty("card_name")
        private String cardName; // 예: KB국민카드

        @JsonProperty("card_number")
        private String cardNumber; // 마스킹된 카드 번호

        @JsonProperty("customer_uid")
        private String customerUid; // 빌링키 (member_...)

        @JsonProperty("pg_provider")
        private String pgProvider; // 예: tosspayments

        @JsonProperty("inserted")
        private Long inserted;

        @JsonProperty("updated")
        private Long updated;
    }
}