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

    public Optional<BookmarkedVideoVO> getVideoBookmarkInfo(BookmarkedVideoVO bookmarkedVideoVO){return bookmarkedVideoMapper.select(bookmarkedVideoVO);}

    public void doBookmarkVideo(BookmarkedVideoVO bookmarkedVideoVO){
        bookmarkedVideoMapper.insert(bookmarkedVideoVO);
    }

    public void eraseBookmarkedVideo(BookmarkedVideoVO bookmarkedVideoVO){
        bookmarkedVideoMapper.delete(bookmarkedVideoVO);
    }
}
