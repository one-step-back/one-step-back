package com.app.onestepback.service;

import com.app.onestepback.domain.*;
import com.app.onestepback.repository.ArtistDAO;
import com.app.onestepback.repository.PostDAO;
import com.app.onestepback.repository.PostTagDAO;
import com.app.onestepback.repository.VideoPostDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VideoPostServiceImpl implements VideoPostService {
    private final VideoPostDAO videoPostDAO;
    private final PostTagDAO postTagDAO;
    private final ArtistDAO artistDAO;
    private final PostDAO postDAO;

    @Override
    public Optional<ArtistDTO> getArtist(Long memberId) {
        return artistDAO.getArtist(memberId);
    }

    @Override
    public int getPostCount(Long memberId) {
        return videoPostDAO.getCountOfVideo(memberId);
    }

    @Override
    public List<VideoPostDTO> getAllVideos(Long memberId, Pagination pagination) {
        List<VideoPostDTO> videos = videoPostDAO.getAllVideos(memberId, pagination);

        for (VideoPostDTO video : videos) {
            List<String> tags = postTagDAO.getAllTags(video.getId());

            if (!tags.isEmpty()) {
                video.setTag1(tags.get(0));
            }
            if (tags.size() >= 2) {
                video.setTag2(tags.get(1));
            }
            if (tags.size() >= 3) {
                video.setTag3(tags.get(2));
            }
            if (tags.size() >= 4) {
                video.setTag4(tags.get(3));
            }
            if (tags.size() >= 5) {
                video.setTag5(tags.get(4));
            }
        }
        return videos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePost(VideoPostDTO videoPostDTO, int numberOfTags) {
        videoPostDAO.savePost(videoPostDTO);
        videoPostDAO.saveVideoPost(videoPostDTO);

        for (int i = 1; i <= numberOfTags; i++) {
            try {
                PostTagVO postTagVO = new PostTagVO();

                String tagName = "tag" + i;
                Field field = videoPostDTO.getClass().getDeclaredField(tagName);
                field.setAccessible(true);

                postTagVO.setPostId(videoPostDTO.getId());
                postTagVO.setPostTagName((String) field.get(videoPostDTO));

                postTagDAO.savePostTag(postTagVO);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
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
        for (int i = 1; i <= numberOfTags; i++) {
            try {
                PostTagVO postTagVO = new PostTagVO();

                String tagName = "tag" + i;
                Field field = videoPostDTO.getClass().getDeclaredField(tagName);
                field.setAccessible(true);

                postTagVO.setPostId(videoPostDTO.getId());
                postTagVO.setPostTagName((String) field.get(videoPostDTO));

                postTagDAO.savePostTag(postTagVO);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void viewCountUp(Long id) {
        postDAO.viewCountUp(id);
    }

    @Override
    public void erasePost(Long id) {
        postDAO.erasePost(id);
    }
}
