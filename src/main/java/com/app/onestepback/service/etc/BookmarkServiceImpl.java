package com.app.onestepback.service.etc;

import com.app.onestepback.domain.vo.BookmarkedArtistPostVO;
import com.app.onestepback.domain.vo.BookmarkedVideoVO;
import com.app.onestepback.repository.BookmarkedArtistPostDAO;
import com.app.onestepback.repository.BookmarkedVideoDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {
    private final BookmarkedArtistPostDAO bookmarkedArtistPostDAO;
    private final BookmarkedVideoDAO bookmarkedVideoDAO;

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

    @Override
    public Optional<BookmarkedVideoVO> checkVideoBookmarkInfo(BookmarkedVideoVO bookmarkedVideoVO) {
        return bookmarkedVideoDAO.getVideoBookmarkInfo(bookmarkedVideoVO);
    }

    @Override
    public void doBookmarkVideo(BookmarkedVideoVO bookmarkedVideoVO) {
        bookmarkedVideoDAO.doBookmarkVideo(bookmarkedVideoVO);
    }

    @Override
    public void eraseBookmarkedVideo(BookmarkedVideoVO bookmarkedVideoVO) {
        bookmarkedVideoDAO.eraseBookmarkedVideo(bookmarkedVideoVO);
    }
}
