package com.app.onestepback.domain.dto.feed;

import com.app.onestepback.global.util.FileUrlSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@Data
public class FeedFileDTO {
    private String id;
    @JsonSerialize(using = FileUrlSerializer.class)
    private String filePath;
    private String fileType;

    public boolean isVideo() {
        return "VIDEO".equals(this.fileType);
    }
}