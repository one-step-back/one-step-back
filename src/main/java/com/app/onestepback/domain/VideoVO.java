package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class VideoVO {
    private Long id;
    private Long memberId;
    private String videoTitle;
    private String videoContent;
    private String videoLink;
    private Long videoViewCount; //디폴트 = 0
    private String videoWriteTime;
    private String videoUpdateTime;
}
