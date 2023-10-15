package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class MemberVO {
    private Long id;
    private String memberEmail;
    private String memberPassword;
    private String memberNickname;
    private String memberKakaoProfileUrl;
    private String memberProfileName;
    private String memberProfilePath;
    private Long memberPaymentTotal; //디폴트 = 0
    private String memberCreateTime;
    private String memberUpdateTime;
    private String memberIntroduction;
    private String memberStatus; //디폴트 = "ACTIVE"
}
