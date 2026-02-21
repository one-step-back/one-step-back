package com.app.onestepback.domain.dto.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KakaoTokenResponse {

    // 1. 액세스 토큰 (The Master Key)
    @JsonProperty("access_token")
    private String accessToken;

    // 2. 토큰 타입
    @JsonProperty("token_type")
    private String tokenType;

    // 3. 리프레시 토큰
    @JsonProperty("refresh_token")
    private String refreshToken;

    // 4. 만료 시간
    @JsonProperty("expires_in")
    private int expiresIn;

    // 5. 리프레시 토큰 만료 시간
    @JsonProperty("refresh_token_expires_in")
    private int refreshTokenExpiresIn;
}