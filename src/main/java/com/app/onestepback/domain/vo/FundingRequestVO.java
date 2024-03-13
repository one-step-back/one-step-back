package com.app.onestepback.domain.vo;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class FundingRequestVO {
//    펀딩 요청 id
    private Long id;
//    아티스트 번호
    private Long memberId;
//    작성자 번호
    private Long writerId;
//    요청 펀딩 제목
    private String fundingRequestTitle;
//    요청 펀딩 내용
    private String fundingRequestContent;
//    요청 펀딩 상태
    private String fundingRequestStatus; // (디폴트)요청 상태 - REQUESTED / 요청 거절 - REJECTED / 요청 수락 - ACCEPTED
}
