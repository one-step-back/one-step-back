package com.app.onestepback.domain.vo;

import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Getter
@NoArgsConstructor
public class PostFileVO {
//    첨부파일 id
    private Long id;
//    게시글 번호
    @Setter
    private Long postId;
//    파일 이름
    private String fileName;
//    파일 경로
    private String filePath;

    @Builder
    public PostFileVO(Long id, Long postId, String fileName, String filePath) {
        this.id = id;
        this.postId = postId;
        this.fileName = fileName;
        this.filePath = filePath;
    }
}
