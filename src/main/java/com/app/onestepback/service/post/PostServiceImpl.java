package com.app.onestepback.service.post;

import com.app.onestepback.domain.dto.PostDTO;
import com.app.onestepback.repository.PostDAO;
import com.app.onestepback.util.YouTubeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostDAO postDAO;

    private final YouTubeUtil youTubeUtil;

    @Override
    public List<PostDTO> getPosts(int limit) {
        List<PostDTO> contents = postDAO.getPosts(limit);

        contents.forEach(content -> {
            String videoLink = content.getVideoLink();

            if (videoLink != null) {
                content.setVideoThumbnail(youTubeUtil.getYouTubeThumbnailUrl(videoLink));
            }
        });

        return contents;
    }
}
