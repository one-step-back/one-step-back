package com.app.onestepback.repository;

import com.app.onestepback.domain.vo.BookmarkedArtistPostVO;
import com.app.onestepback.mapper.BookmarkedArtistPostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BookmarkedArtistPostDAO {
    private final BookmarkedArtistPostMapper bookmarkedArtistPostMapper;

    public Optional<BookmarkedArtistPostVO> getArtistPostBookmarkInfo(BookmarkedArtistPostVO bookmarkedArtistPostVO){return bookmarkedArtistPostMapper.select(bookmarkedArtistPostVO);}

    public void doBookmarkArtistPost(BookmarkedArtistPostVO bookmarkedArtistPostVO){
        bookmarkedArtistPostMapper.insert(bookmarkedArtistPostVO);
    }

    public void eraseBookmarkedArtistPost(BookmarkedArtistPostVO bookmarkedArtistPostVO){
        bookmarkedArtistPostMapper.delete(bookmarkedArtistPostVO);
    }
}
