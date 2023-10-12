package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class LikeVideoVO {
    private Long id;
    private Long videoId;
    private Long memberId;
}
