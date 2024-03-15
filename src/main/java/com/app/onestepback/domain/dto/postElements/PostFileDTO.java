package com.app.onestepback.domain.dto.postElements;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostFileDTO {
    private Long fileId;
    private String fileName;
    private String filePath;
}
