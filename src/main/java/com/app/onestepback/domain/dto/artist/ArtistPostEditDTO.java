package com.app.onestepback.domain.dto.artist;

import com.app.onestepback.domain.dto.postElements.PostFileDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ArtistPostEditDTO {
    private Long postId;
    private Long artistId;
    private String title;
    private String subtitle;
    private String content;
    private String category;

    private List<String> tags = new ArrayList<>();

    private List<PostFileDTO> files = new ArrayList<>();

    private List<Long> deletedFiles = new ArrayList<>();
    private List<MultipartFile> newFiles = new ArrayList<>();
}
