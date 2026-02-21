package com.app.onestepback.domain.dto.member;

import com.app.onestepback.domain.model.MemberVO;
import com.app.onestepback.global.util.FileUrlSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class SessionUser implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String memberEmail;
    private String memberNickname;
    private String memberKakaoProfileUrl;
    private String memberProfileId;
    @JsonSerialize(using = FileUrlSerializer.class)
    private String profilePath;
    private String memberIntroduction;
    private boolean hasPaymentMethod;
    private boolean artist;

    // 비밀번호 존재 여부 (true: 있음, false: 없음-카카오)
    private boolean hasPassword;

    public SessionUser(MemberVO vo) {
        this.id = vo.getId();
        this.memberEmail = vo.getMemberEmail();
        this.memberNickname = vo.getMemberNickname();
        this.memberKakaoProfileUrl = vo.getMemberKakaoProfileUrl();
        this.memberProfileId = vo.getMemberProfileId();
        this.memberIntroduction = vo.getMemberIntroduction();
        this.profilePath = determineProfilePath();
        this.hasPaymentMethod = vo.isPaymentMethodExists();
        this.artist = vo.isArtist();

        // DB에 비밀번호가 존재하면 true, 없으면 false
        this.hasPassword = (vo.getMemberPassword() != null && !vo.getMemberPassword().isBlank());
    }

    private String determineProfilePath() {
        if (memberProfileId != null && !memberProfileId.isBlank()) {
            return "/api/v1/files/" + memberProfileId;
        }
        if (memberKakaoProfileUrl != null && !memberKakaoProfileUrl.isBlank()) {
            return memberKakaoProfileUrl;
        }
        return "/images/profile/avatar_blank.png";
    }
}