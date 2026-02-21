package com.app.onestepback.domain.type.artist;

public enum ArtistSortType {
    NEW,        // 최신순
    POPULAR,    // 인기순 (팔로워)
    RANDOM;     // 랜덤 (기본)

    // 문자열 파라미터를 안전하게 Enum으로 변환하는 팩토리 메서드 (선택사항)
    public static ArtistSortType from(String value) {
        if (value == null || value.isBlank()) {
            return RANDOM;
        }
        try {
            return ArtistSortType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return RANDOM; // 이상한 값이 들어오면 랜덤으로 방어
        }
    }
}