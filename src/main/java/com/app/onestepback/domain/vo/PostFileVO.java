package com.app.onestepback.domain.vo;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class PostFileVO {
//    첨부파일 id
    private Long id;
//    게시글 번호
    private Long postId;
//    파일 이름
    private String fileName;
//    파일 경로
    private String filePath;
}
