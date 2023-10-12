package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class CrowdFundingVO {
    private Long id;
    private Long memberId;
    private String fundingTitle;
    private String fundingContent;
    private String fundingImgName;
    private String fundingImgPath;
    private Long fundingSumCollected; //디폴트 = 0
    private Long fundingTargetAmount;
    private String fundingStatus; //디폴트 = 'ONGOING'
}
