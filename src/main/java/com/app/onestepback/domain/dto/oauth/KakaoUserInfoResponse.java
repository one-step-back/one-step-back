package com.app.onestepback.domain.dto.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserInfoResponse {

    // 1. 회원 번호
    private Long id;

    // 2. 카카오 계정 정보 (껍데기)
    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoAccount {
        // 이메일
        private String email;

        // 프로필 정보
        private Profile profile;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Profile {
            private String nickname;
            @JsonProperty("profile_image_url")
            private String profileImageUrl;
            @JsonProperty("thumbnail_image_url")
            private String thumbnailImageUrl;
        }
    }
}