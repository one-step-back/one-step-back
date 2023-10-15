package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ArtistVO {
    private Long memberId;
    private String artistBlogName;
    private String artistDescription;
    private String blogImgName;
    private String blogImgPath;
    private String artistCreateTime;
    private String artistUpdateTime;
}
