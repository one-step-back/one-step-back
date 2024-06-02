package com.app.onestepback.domain.dto;

import com.app.onestepback.domain.dto.postElements.PostTagDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Data
@NoArgsConstructor
public class PostDTO {
    private Long postId;
    private Long artistId;
    private String title;
    private String content;
    private String category;
    private LocalDateTime createdTime;

    private Long likeCount;
    private Long replyCount;
    private Long viewCount;

    private String videoLink;
    private String videoThumbnail;
    private String fileName;
    private String filePath;

    private String nickname;
    private String kakaoProfileUrl;
    private String profileName;
    private String profilePath;

    private List<String> tags = new ArrayList<>();
}
