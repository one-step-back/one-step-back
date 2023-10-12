package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class FundingRequestVO {
    private Long id;
    private Long memberId;
    private Long artistId;
    private String requestFundingTitle;
    private String requestFundingContent;
    private String requestFundingStatus; //디폴트 = 'REQUESTED'
}
