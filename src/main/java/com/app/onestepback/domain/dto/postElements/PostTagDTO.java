package com.app.onestepback.domain.dto.postElements;

import lombok.*;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
public class PostTagDTO {
    private Long postId;
    private String postTagName;
}
