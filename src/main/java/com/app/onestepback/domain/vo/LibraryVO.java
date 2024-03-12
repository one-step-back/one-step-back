package com.app.onestepback.domain.vo;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class LibraryVO {
    private Long id;
    private Long memberId;
    private Long postId;
    private String libraryAddedTime;
}
