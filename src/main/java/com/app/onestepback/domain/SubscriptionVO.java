package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class SubscriptionVO {
    private Long artistId;
    private Long memberId;
}
