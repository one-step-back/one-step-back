package com.app.onestepback.domain.model;

import com.app.onestepback.domain.type.member.MemberStatus;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MemberVO {
    private Long id;
    private String memberEmail;
    private String memberPassword;
    private String memberNickname;

    private Long memberKakaoId;
    private String memberKakaoProfileUrl;

    private String memberProfileId;
    private String memberIntroduction;
    private boolean paymentMethodExists;
    private Long memberPaymentTotal;
    private String memberCreateTime;
    private String memberUpdateTime;
    private MemberStatus memberStatus;

    private boolean isArtist;

    public void setKakao(Long kakaoId, String kakaoProfileUrl) {
        this.memberKakaoId = kakaoId;
        this.memberKakaoProfileUrl = kakaoProfileUrl;
    }
}
