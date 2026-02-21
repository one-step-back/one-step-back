package com.app.onestepback.service.bank;

/**
 * 외부 금융망 또는 결제 대행사(PG)를 통해 계좌의 예금주 실명 일치 여부를 검증하는 서비스 인터페이스입니다.
 */
public interface BankVerificationService {

    /**
     * 입력된 계좌 정보가 실제 예금주와 일치하는지 확인합니다.
     *
     * @param bankName      조회 대상 은행명 (예: "신한은행")
     * @param accountNumber 하이픈(-)이 제외된 계좌 번호
     * @param accountHolder 검증할 예금주 실명
     * @return 정보가 일치하면 true, 불일치하거나 통신에 실패하면 false 반환
     */
    boolean verifyAccountHolder(String bankName, String accountNumber, String accountHolder);
}