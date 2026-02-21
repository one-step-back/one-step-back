package com.app.onestepback.global.util;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 정렬 가능한 고유 식별자(ULID: Universally Unique Lexicographically Sortable Identifier)를
 * 생성하기 위한 시스템 전역 유틸리티 클래스입니다.
 * <p>
 * 밀리초 단위의 타임스탬프와 보안 난수(Random)를 결합하여 UUID의 한계를 보완하며,
 * 대규모 분산 환경에서 시간 순서에 따른 안정적인 데이터 정렬을 보장합니다.
 * </p>
 */
public final class ULIDs {
    private ULIDs() {
    }

    private static final char[] ALPHABET = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();

    private static final AtomicLong LAST_TIME = new AtomicLong(-1L);
    private static volatile int lastRandHi = 0;
    private static volatile long lastRandLo = 0L;

    private static final SecureRandom SECURE = new SecureRandom();

    /**
     * 시간 정렬이 가능한 고속 ULID를 생성합니다.
     *
     * @return 26자리 Base32 문자열로 구성된 고유 식별자
     */
    public static String newUlid() {
        long time = System.currentTimeMillis();
        int randHi = ThreadLocalRandom.current().nextInt() & 0xFFFF;
        long randLo = ThreadLocalRandom.current().nextLong();
        return encode(time, randHi, randLo);
    }

    /**
     * 동일 밀리초 내에서도 순차적인 오름차순 정렬을 완벽하게 보장하는 단조 증가형 ULID를 생성합니다.
     *
     * @return 26자리 Base32 문자열로 구성된 단조 증가 식별자
     */
    public static synchronized String newMonotonicUlid() {
        long now = System.currentTimeMillis();
        long last = LAST_TIME.get();

        int randHi;
        long randLo;

        if (now > last) {
            LAST_TIME.set(now);
            randHi = SECURE.nextInt() & 0xFFFF;
            randLo = ThreadLocalRandom.current().nextLong();
        } else {
            long lo = lastRandLo + 1;
            int hi = lastRandHi;

            if (lo == 0) {
                hi = (hi + 1) & 0xFFFF;
                if (hi == 0) {
                    do {
                        now = System.currentTimeMillis();
                    } while (now <= last);
                    LAST_TIME.set(now);
                    hi = SECURE.nextInt() & 0xFFFF;
                    lo = ThreadLocalRandom.current().nextLong();
                }
            }
            randHi = hi;
            randLo = lo;
        }

        lastRandHi = randHi;
        lastRandLo = randLo;

        return encode(now, randHi, randLo);
    }

    /**
     * 타임스탬프와 난수 데이터를 Base32 문자열 포맷으로 인코딩합니다.
     */
    private static String encode(long timeMs, int randHi16, long randLo64) {
        byte[] b = new byte[16];

        b[0] = (byte) ((timeMs >>> 40) & 0xFF);
        b[1] = (byte) ((timeMs >>> 32) & 0xFF);
        b[2] = (byte) ((timeMs >>> 24) & 0xFF);
        b[3] = (byte) ((timeMs >>> 16) & 0xFF);
        b[4] = (byte) ((timeMs >>>  8) & 0xFF);
        b[5] = (byte) ( timeMs         & 0xFF);

        b[6] = (byte) ((randHi16 >>> 8) & 0xFF);
        b[7] = (byte) ( randHi16        & 0xFF);

        b[8]  = (byte) ((randLo64 >>> 56) & 0xFF);
        b[9]  = (byte) ((randLo64 >>> 48) & 0xFF);
        b[10] = (byte) ((randLo64 >>> 40) & 0xFF);
        b[11] = (byte) ((randLo64 >>> 32) & 0xFF);
        b[12] = (byte) ((randLo64 >>> 24) & 0xFF);
        b[13] = (byte) ((randLo64 >>> 16) & 0xFF);
        b[14] = (byte) ((randLo64 >>>  8) & 0xFF);
        b[15] = (byte) ( randLo64         & 0xFF);

        char[] out = new char[26];
        int bitPos = -2;

        for (int i = 0; i < 26; i++) {
            int v = 0;
            for (int k = 0; k < 5; k++) {
                int idx = bitPos + k;
                int bit;
                if (idx < 0) {
                    bit = 0;
                } else {
                    int byteIdx = idx / 8;
                    int bitInByte = 7 - (idx % 8);
                    bit = (b[byteIdx] >>> bitInByte) & 0x01;
                }
                v = (v << 1) | bit;
            }
            out[i] = ALPHABET[v & 0x1F];
            bitPos += 5;
        }
        return new String(out);
    }
}