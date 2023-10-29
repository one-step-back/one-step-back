package com.app.onestepback.service;

import com.app.onestepback.domain.BookmarkedArtistPostVO;
import com.app.onestepback.domain.BookmarkedVideoVO;

import java.util.Optional;

public interface BookmarkService {
    public Optional<BookmarkedArtistPostVO> checkArtistPostBookmarkInfo(BookmarkedArtistPostVO bookmarkedArtistPostVO);

    public void doBookmarkArtistPost(BookmarkedArtistPostVO bookmarkedArtistPostVO);

    public void eraseBookmarkedArtistPost(BookmarkedArtistPostVO bookmarkedArtistPostVO);

    public Optional<BookmarkedVideoVO> checkVideoBookmarkInfo(BookmarkedVideoVO bookmarkedVideoVO);

    public void doBookmarkVideo(BookmarkedVideoVO bookmarkedVideoVO);

    public void eraseBookmarkedVideo(BookmarkedVideoVO bookmarkedVideoVO);
}
