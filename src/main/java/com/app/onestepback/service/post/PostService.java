package com.app.onestepback.service.post;

import com.app.onestepback.domain.dto.PostDTO;

import java.util.List;

public interface PostService {
    List<PostDTO> getPosts(int limit);
}
