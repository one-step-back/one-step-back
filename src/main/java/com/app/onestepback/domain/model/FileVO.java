package com.app.onestepback.domain.model;

import lombok.*;

@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class FileVO {
    private String id;
    private String fileOriginalName;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileExtension;
    private Long targetId;
    private String targetGb;
    private Long fileOwnerId;
    private String createTime;
}