package com.app.onestepback.global.util;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * PBKDF2 알고리즘을 활용하여 사용자 비밀번호의 단방향 암호화 및 검증을 수행하는 유틸리티 클래스입니다.
 * <p>
 * 무작위 솔트(Salt)를 부여하고 해시 반복 연산을 수행하여 레인보우 테이블 공격 및
 * 무차별 대입 공격으로부터 사용자 인증 정보를 안전하게 보호합니다.
 * </p>
 */
@Component
public class PasswordEncoder {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 128;
    private static final int SALT_LENGTH = 16;

    /**
     * 평문 비밀번호에 고유 솔트를 적용하여 암호화된 문자열을 생성합니다.
     *
     * @param rawPassword 사용자가 입력한 평문 비밀번호
     * @return 생성된 솔트와 해시값이 결합된 문자열 구조 ("Salt:Hash")
     */
    public String encode(String rawPassword) {
        try {
            byte[] salt = getSalt();
            byte[] hash = hash(rawPassword.toCharArray(), salt);

            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hash);

            return saltBase64 + ":" + hashBase64;
        } catch (Exception e) {
            throw new RuntimeException("비밀번호 암호화 처리 중 시스템 오류가 발생하였습니다.", e);
        }
    }

    /**
     * 입력된 비밀번호가 저장된 암호화 데이터와 일치하지 않는지 검증합니다.
     * <p>
     * 타이밍 공격(Timing Attack)을 방지하기 위해 상수 시간(Constant Time) 비교 로직을 적용합니다.
     * </p>
     *
     * @param rawPassword    사용자가 입력한 평문 비밀번호
     * @param storedPassword 데이터베이스에 보관된 암호화 문자열
     * @return 비밀번호 일치 여부
     */
    public boolean isMismatch(String rawPassword, String storedPassword) {
        try {
            String[] parts = storedPassword.split(":");
            if (parts.length != 2) return true;

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] storedHash = Base64.getDecoder().decode(parts[1]);

            byte[] computedHash = hash(rawPassword.toCharArray(), salt);

            return !slowEquals(storedHash, computedHash);
        } catch (Exception e) {
            return true;
        }
    }

    private byte[] getSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    private byte[] hash(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        return skf.generateSecret(spec).getEncoded();
    }

    private boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }
}