package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.artist.ArtistDTO;
import com.app.onestepback.domain.dto.artist.ArtistPostDTO;
import com.app.onestepback.domain.dto.artist.ArtistPostRegisterDTO;
import com.app.onestepback.domain.vo.Pagination;
import com.app.onestepback.domain.vo.PostFileVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ArtistPostService {
    public Optional<ArtistDTO> getArtist(Long memberId);

    public int getPostCount(Long memberId);

    public List<ArtistPostDTO> getAllPosts(Long memberId, Pagination pagination);

    public List<String> getAllTags(Long postId);

    public void savePost(ArtistPostRegisterDTO artistPostRegisterDTO) throws IOException;

    public ArtistPostDTO getPost(Long id);

    public Optional<ArtistPostDTO> getPrevPost(ArtistPostDTO artistPostDTO);

    public Optional<ArtistPostDTO> getNextPost(ArtistPostDTO artistPostDTO);

    public void editPost(ArtistPostDTO artistPostDTO, int numberOfTags, List<String> uuids, List<MultipartFile> uploadFiles);

    public List<PostFileVO> getAllFiles(Long postId);

    public void erasePost(Long id);
}
