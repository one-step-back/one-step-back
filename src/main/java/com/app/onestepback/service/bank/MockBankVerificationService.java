package com.app.onestepback.service.bank;

import org.springframework.stereotype.Service;

@Service
public class MockBankVerificationService implements BankVerificationService {
    @Override
    public boolean verifyAccountHolder(String bankName, String accountNumber, String accountHolder) {
        // [TEST RULE] 계좌번호가 '0000'으로 시작하면 무조건 실패 처리
        if (accountNumber.startsWith("0000")) {
            return false;
        }
        return true; // 그 외에는 성공
    }
}