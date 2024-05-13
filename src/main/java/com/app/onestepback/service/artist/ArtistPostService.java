package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.artist.*;
import com.app.onestepback.domain.vo.Pagination;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ArtistPostService {
    ArtistDetailDTO getArtist(Long memberId);

    int getPostCount(Long memberId);

    List<ArtistPostListDTO> getArtistPostsPage(Long artistId, Long viewerId, Pagination pagination);

    public List<String> getAllTags(Long postId);

    void savePost(ArtistPostRegisterDTO artistPostRegisterDTO) throws IOException;

    ArtistPostDetailDTO getPostDetail(Long artistId, Long postId);

    public void editPost(ArtistPostDTO artistPostDTO, int numberOfTags, List<String> uuids, List<MultipartFile> uploadFiles);

    public void erasePost(Long id);
}
