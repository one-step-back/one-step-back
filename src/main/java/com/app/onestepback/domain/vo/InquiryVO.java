package com.app.onestepback.domain.vo;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class InquiryVO {
//    문의 id
    private Long id;
//    회원 번호
    private Long memberId;
//    문의 제목
    private String inquiryTitle;
//    문의 내용
    private String inquiryContent;
//    문의 작성 시간
    private String inquiryWriteTime;
//    문의 답변일
    private String inquiryAnswerTime;
}
