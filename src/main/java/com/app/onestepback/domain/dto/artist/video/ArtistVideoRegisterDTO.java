package com.app.onestepback.domain.dto.artist.video;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ArtistVideoRegisterDTO {
    private Long postId;
    private Long artistId;
    private String title;
    private String videoLink;
    private String content;
    private String category;
    private long viewCount = 0;

    List<String> tags = new ArrayList<>();
}
