package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class VideoTagVO {
    private Long id;
    private Long videoId;
    private String videoTagName;
}
