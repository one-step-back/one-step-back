package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ArtistDTO {
    private Long id;
    private String memberEmail;
    private String memberPassword;
    private String memberStatus; //디폴트 = "ACTIVE"
    private String memberNickname;
    private String memberKakaoProfileUrl;
    private String memberProfileName;
    private String memberProfilePath;
    private Long memberAmountMoney; //디폴트 = 0
    private String memberCreateTime;
    private String memberUpdateTime;

    private String artistBlogName;
    private String artistDescription;
    private String blogImgName;
    private String blogImgPath;
    private String artistCreateTime;
    private String artistUpdateTime;
}
