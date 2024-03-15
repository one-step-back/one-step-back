package com.app.onestepback.domain.dto.artist;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ArtistPostRegisterDTO {
    private Long postId;
    private Long memberId;
    private String title;
    private String subtitle;
    private String content;
    private String category;
    private long viewCount = 0;

    List<String> tags = new ArrayList<>();

    List<MultipartFile> files = new ArrayList<>();
}
