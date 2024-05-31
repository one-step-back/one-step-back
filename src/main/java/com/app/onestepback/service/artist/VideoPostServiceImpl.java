package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.VideoPostDTO;
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

import java.util.List;
import java.util.Optional;
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
    @Transactional(rollbackFor = Exception.class)
    public VideoPostDTO getVideoPost(Long id) {
        postDAO.viewCountUp(id);
        VideoPostDTO post = videoPostDAO.getVideoPost(id);

        List<String> tags = postTagDAO.getAllTags(post.getId());

        if (!tags.isEmpty()) {
            post.setTag1(tags.get(0));
        }
        if (tags.size() >= 2) {
            post.setTag2(tags.get(1));
        }
        if (tags.size() >= 3) {
            post.setTag3(tags.get(2));
        }
        if (tags.size() >= 4) {
            post.setTag4(tags.get(3));
        }
        if (tags.size() >= 5) {
            post.setTag5(tags.get(4));
        }

        return post;
    }

    @Override
    public Optional<VideoPostDTO> getPrevPost(VideoPostDTO videoPostDTO) {
        return videoPostDAO.getPrevPost(videoPostDTO);
    }

    @Override
    public Optional<VideoPostDTO> getNextPost(VideoPostDTO videoPostDTO) {
        return videoPostDAO.getNextPost(videoPostDTO);
    }

    @Override
    public void editVideoPost(VideoPostDTO videoPostDTO, int numberOfTags) {
        videoPostDAO.editVideoPost(videoPostDTO);
        videoPostDAO.editVideoLink(videoPostDTO);

        postTagDAO.deletePostTag(videoPostDTO.getId());
//        for (int i = 1; i <= numberOfTags; i++) {
//            try {
//                PostTagVO postTagVO = new PostTagVO();
//
//                String tagName = "tag" + i;
//                Field field = videoPostDTO.getClass().getDeclaredField(tagName);
//                field.setAccessible(true);
//
//                postTagVO.setPostId(videoPostDTO.getId());
//                postTagVO.setPostTagName((String) field.get(videoPostDTO));
//
//                postTagDAO.savePostTag(postTagVO);
//            } catch (NoSuchFieldException | IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public void viewCountUp(Long id) {
        postDAO.viewCountUp(id);
    }

    @Override
    public void erasePost(Long id) {
//        postDAO.erasePost(id);
    }
}
