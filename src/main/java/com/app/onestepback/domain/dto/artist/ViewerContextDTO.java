package com.app.onestepback.domain.dto.artist;

import lombok.Data;

@Data
public class ViewerContextDTO {
    private boolean following;     // 팔로우 여부
    private boolean subscribed;    // 구독 여부
    
    // 구독자일 경우에만 채워지는 상세 정보
    private SubscriptionDetailDTO subscriptionDetail; 
}
