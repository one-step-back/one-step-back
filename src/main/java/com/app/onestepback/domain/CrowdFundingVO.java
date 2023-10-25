package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class CrowdFundingVO {
    //    크라우드 펀딩  id
    private Long id;
    //    회원 번호(아티스트)
    private Long memberId;
    //    회원 번호(작성자)
    private Long writerId;
    //    펀딩 제목
    private String fundingTitle;
    //    펀딩 내용
    private String fundingContent;
    //    크라우드 펀딩 사진 이름
    private String fundingImgName;
    //    크라우드 펀딩 이미지 경로
    private String fundingImgPath;
    //    현재 모인 금액
    private Long fundingSumCollected; //디폴트 = 0
    //    목표 금액
    private Long fundingTargetAmount;
    //    펀딩 상태
    private String fundingStatus; //(디폴트)진행 중 : ONGOING / 종료 : ENDED
    //    작성 시간
    private String fundingWriteTime;
    //    수정 시간
    private String fundingUpdateTime;
    //    종료 시간
    private String fundingTimeLimit;
}
