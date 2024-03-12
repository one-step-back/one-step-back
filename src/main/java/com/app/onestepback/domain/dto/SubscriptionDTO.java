package com.app.onestepback.domain.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class SubscriptionDTO {
    //    회원 번호
    private Long artistId;
    //    회원 번호
    private Long memberId;


    //    아티스트 블로그 이름
    private String artistBlogName;
    //    아티스트 소개
    private String artistDescription;
    //    블로그 사진 이름
    private String blogImgName;
    //    블로그 사진 경로
    private String blogImgPath;
    //    아티스트 생성일자(승격)
    private String artistCreateTime;
    //    아티스트 정보 수정일자


    //    회원 닉네임
    private String memberNickname;
    //    카카오 프로필 사진
    private String memberKakaoProfileUrl;
    //    프로필 사진 이름
    private String memberProfileName;
    //    프로필 사진 경로
    private String memberProfilePath;
    //    회원소개
    private String memberIntroduction;
    //    총 결제 금액, 디폴트 = 0
    private Long memberPaymentTotal;
    //    가입 일자
    private String memberCreateTime;
    //    회원 정보 수정 일자
    private String memberUpdateTime;
    //    회원 상태, (디폴트) 활동 중 - ACTIVE / 회원 탈퇴를 했을 시 - DISABLE / 정지 되었을 시 - BLOCKED
    private String memberStatus;
}
