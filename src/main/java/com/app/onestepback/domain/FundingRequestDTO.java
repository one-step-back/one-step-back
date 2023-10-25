package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class FundingRequestDTO {
    //    펀딩 요청 id
    private Long id;
    //    아티스트 번호
    private Long memberId;
    //    작성자 번호
    private Long writerId;
    //    요청 펀딩 제목
    private String requestFundingTitle;
    //    요청 펀딩 내용
    private String requestFundingContent;
    //    요청 펀딩 상태
    private String requestFundingStatus; // (디폴트)요청 상태 - REQUESTED / 요청 거절 - REJECTED / 요청 수락 - ACCEPTED

//        멤버 VO와 조인
    //    회원 번호
    private Long writerMemberId;
    //    회원 닉네임
    private String memberNickname;
    //    카카오 프로필 사진
    private String memberKakaoProfileUrl;
    //    프로필 사진 이름
    private String memberProfileName;
    //    프로필 사진 경로
    private String memberProfilePath;
}
