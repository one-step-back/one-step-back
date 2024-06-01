package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.artist.video.ArtistVideoDetailDTO;
import com.app.onestepback.domain.dto.artist.video.ArtistVideoEditDTO;
import com.app.onestepback.domain.dto.artist.video.ArtistVideoListDTO;
import com.app.onestepback.domain.dto.artist.video.ArtistVideoRegisterDTO;
import com.app.onestepback.domain.vo.Pagination;
import com.app.onestepback.domain.vo.PostTagVO;
import com.app.onestepback.repository.PostDAO;
import com.app.onestepback.repository.PostTagDAO;
import com.app.onestepback.repository.VideoPostDAO;
import com.app.onestepback.util.TimeUtil;
import com.app.onestepback.util.YouTubeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoPostServiceImpl implements VideoPostService {
    private final VideoPostDAO videoPostDAO;
    private final PostTagDAO postTagDAO;
    private final PostDAO postDAO;

    private final TimeUtil timeUtil;
    private final YouTubeUtil youTubeUtil;

    @Override
    public int getPostCount(Long artistId) {
        return videoPostDAO.getCountOfVideo(artistId);
    }

    @Override
    public List<ArtistVideoListDTO> getArtistVideoPage(Long artistId, Long viewerId, Pagination pagination) {
        List<ArtistVideoListDTO> contents = videoPostDAO.getArtistVideoPage(artistId, viewerId, pagination);

        contents.forEach(content -> {
            content.setTimeGap(timeUtil.getTimeGap(content.getWriteTime()));
            content.setVideoThumbnail(youTubeUtil.getYouTubeThumbnailUrl(content.getVideoLink()));
        });

        return contents;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePost(ArtistVideoRegisterDTO artistVideoRegisterDTO) {
        videoPostDAO.savePost(artistVideoRegisterDTO);
        videoPostDAO.saveVideoPost(artistVideoRegisterDTO.getPostId(), artistVideoRegisterDTO.getVideoLink());

        List<PostTagVO> postTags = artistVideoRegisterDTO.getTags().stream()
                .map(tagName -> PostTagVO.builder()
                        .postId(artistVideoRegisterDTO.getPostId())
                        .postTagName(tagName)
                        .build())
                .collect(Collectors.toList());
        postTagDAO.saveAllTags(postTags);
    }

    @Override
    public ArtistVideoDetailDTO getPostDetail(Long artistId, Long postId, Long viewerId) {
        ArtistVideoDetailDTO content = videoPostDAO.getPost(artistId, postId, viewerId).orElseThrow(
                () -> new NoSuchElementException("해당 게시글을 조회할 수 없음.")
        );

        content.setEmbedLink(youTubeUtil.getYouTubeEmbedLink(content.getVideoLink()));
        return content;
    }

    @Override
    public ArtistVideoEditDTO getEditPost(Long artistId, Long postId) {
        return videoPostDAO.getEditPost(artistId, postId).orElseThrow(
                () -> new NoSuchElementException("해당 게시글을 조회할 수 없음.")
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editVideoPost(ArtistVideoEditDTO artistVideoEditDTO) {
        videoPostDAO.editVideoPost(artistVideoEditDTO);
        videoPostDAO.editVideoLink(artistVideoEditDTO);

        List<String> savedTags = postTagDAO.getAllTags(artistVideoEditDTO.getPostId());
        List<String> tags = artistVideoEditDTO.getTags();

        // 새로운 태그 insert
        List<String> newTags = new ArrayList<>(tags);
        newTags.removeAll(savedTags);

        List<PostTagVO> saveTagsList = new ArrayList<>();

        for (String newTag : newTags) {
            PostTagVO postTagVO = PostTagVO.builder()
                    .postId(artistVideoEditDTO.getPostId())
                    .postTagName(newTag)
                    .build();
            saveTagsList.add(postTagVO);
        }
        if (!saveTagsList.isEmpty()) {
            postTagDAO.saveAllTags(saveTagsList);
        }

        // 삭제되어야 하는 태그 delete
        List<String> deletedTags = new ArrayList<>(savedTags);
        deletedTags.removeAll(tags);

        List<PostTagVO> deleteTagList = new ArrayList<>();

        for (String deleteTag : deletedTags) {
            PostTagVO postTagVO = PostTagVO.builder()
                    .postId(artistVideoEditDTO.getPostId())
                    .postTagName(deleteTag)
                    .build();
            deleteTagList.add(postTagVO);
        }
        if (!deleteTagList.isEmpty()) {
            postTagDAO.deleteAllTags(deleteTagList);
        }
    }

    @Override
    public void viewCountUp(Long id) {
        postDAO.viewCountUp(id);
    }

    @Override
    public void erasePost(Long postId, Long artistId) {
        postDAO.erasePost(postId, artistId);
    }
}
