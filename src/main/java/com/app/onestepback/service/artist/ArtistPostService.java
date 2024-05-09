package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.artist.*;
import com.app.onestepback.domain.vo.Pagination;
import com.app.onestepback.domain.vo.PostFileVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ArtistPostService {
    public Optional<ArtistDTO> getArtist(Long memberId);

    public int getPostCount(Long memberId);

    public List<ArtistPostListDTO> getArtistPostsPage(Long memberId, Pagination pagination);

    public List<String> getAllTags(Long postId);

    public void savePost(ArtistPostRegisterDTO artistPostRegisterDTO) throws IOException;

    ArtistPostDetailDTO getPostDetail(Long artistId, Long postId);

    public void editPost(ArtistPostDTO artistPostDTO, int numberOfTags, List<String> uuids, List<MultipartFile> uploadFiles);

    public void erasePost(Long id);
}
