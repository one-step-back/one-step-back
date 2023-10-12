package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ArtistPostFileVO {
    private Long id;
    private Long artistPostId;
    private String fileName;
    private String filePath;
}
