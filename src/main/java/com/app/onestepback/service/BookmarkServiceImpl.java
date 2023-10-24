package com.app.onestepback.service;

import com.app.onestepback.domain.BookmarkedArtistPostVO;
import com.app.onestepback.repository.BookmarkedArtistPostDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {
    private final BookmarkedArtistPostDAO bookmarkedArtistPostDAO;

    @Override
    public Optional<BookmarkedArtistPostVO> checkArtistPostBookmarkInfo(BookmarkedArtistPostVO bookmarkedArtistPostVO) {
        return bookmarkedArtistPostDAO.getArtistPostBookmarkInfo(bookmarkedArtistPostVO);
    }

    @Override
    public void doBookmarkArtistPost(BookmarkedArtistPostVO bookmarkedArtistPostVO) {
        bookmarkedArtistPostDAO.doBookmarkArtistPost(bookmarkedArtistPostVO);
    }

    @Override
    public void eraseBookmarkedArtistPost(BookmarkedArtistPostVO bookmarkedArtistPostVO) {
        bookmarkedArtistPostDAO.eraseBookmarkedArtistPost(bookmarkedArtistPostVO);
    }
}
