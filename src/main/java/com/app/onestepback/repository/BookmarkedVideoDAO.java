package com.app.onestepback.repository;

import com.app.onestepback.domain.vo.BookmarkedVideoVO;
import com.app.onestepback.mapper.BookmarkedVideoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BookmarkedVideoDAO {
    private final BookmarkedVideoMapper bookmarkedVideoMapper;

    public void doBookmarkVideo(Long postId, Long memberId) {
        bookmarkedVideoMapper.insert(postId, memberId);
    }

    public void eraseBookmarkedVideo(Long postId, Long memberId) {
        bookmarkedVideoMapper.delete(postId, memberId);
    }
}
