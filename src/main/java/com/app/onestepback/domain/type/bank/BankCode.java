package com.app.onestepback.domain.type.bank;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum BankCode {
    // [특수/국책은행]
    KDB("KDB", "002", "KDB산업은행"),
    IBK("IBK", "003", "IBK기업은행"),
    SUHYUP("SUHYUP", "007", "수협은행"),
    NONGHYUP("NONGHYUP", "011", "NH농협은행"),

    // [시중은행]
    KOOKMIN("KOOKMIN", "004", "KB국민은행"),
    WOORI("WOORI", "020", "우리은행"),
    SC("SC", "023", "SC제일은행"),
    CITI("CITI", "027", "한국씨티은행"),
    HANA("HANA", "081", "하나은행"),
    SHINHAN("SHINHAN", "088", "신한은행"),

    // [지방은행]
    DAEGU("DAEGU", "031", "DGB대구은행"),
    BUSAN("BUSAN", "032", "BNK부산은행"),
    GWANGJU("GWANGJU", "034", "광주은행"),
    JEJU("JEJU", "035", "제주은행"),
    JEONBUK("JEONBUK", "037", "전북은행"),
    GYEONGNAM("GYEONGNAM", "039", "BNK경남은행"),

    // [비은행/서민금융]
    SAEMAUL("SAEMAUL", "045", "새마을금고"),
    SHINHYUP("SHINHYUP", "048", "신협"),
    POST("POST", "071", "우체국"),

    // [인터넷전문은행]
    K_BANK("K_BANK", "089", "케이뱅크"),
    KAKAO("KAKAO", "090", "카카오뱅크"),
    TOSS("TOSS", "092", "토스뱅크");

    private final String name;        // Enum Name (DB 저장용, e.g. KOOKMIN)
    private final String code;        // 금융결제원 표준 코드 (API 전송용, e.g. 004)
    private final String description; // 화면 출력용 (e.g. KB국민은행)

    // 문자열(KOOKMIN or 국민은행)로 Enum 찾기 (필요시 사용)
    public static BankCode from(String input) {
        return Arrays.stream(values())
                .filter(b -> b.name.equalsIgnoreCase(input) || b.description.equals(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 은행입니다: " + input));
    }
}