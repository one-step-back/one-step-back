package com.app.onestepback.service;

import com.app.onestepback.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ArtistPostService {
    public Optional<ArtistDTO> getArtist(Long id);

    public int getPostCount(Long memberId);

    public List<ArtistPostDTO> getAllPosts(Long memberId, Pagination pagination);

    public List<String> getAllTags(Long postId);

    public void savePost(ArtistPostDTO artistPostDTO, int numberOfTags, List<String> uuids, List<MultipartFile> uploadFiles);

    public ArtistPostDTO getPost(Long id);

    public Optional<ArtistDTO> getArtistInfo(Long memberId);

    public Optional<ArtistPostDTO> getPrevPost(ArtistPostDTO artistPostDTO);

    public Optional<ArtistPostDTO> getNextPost(ArtistPostDTO artistPostDTO);
}
