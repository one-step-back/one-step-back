package com.app.onestepback.service;

import com.app.onestepback.domain.BookmarkedArtistPostVO;

import java.util.Optional;

public interface BookmarkService {
    public Optional<BookmarkedArtistPostVO> checkArtistPostBookmarkInfo(BookmarkedArtistPostVO bookmarkedArtistPostVO);

    public void doBookmarkArtistPost(BookmarkedArtistPostVO bookmarkedArtistPostVO);

    public void eraseBookmarkedArtistPost(BookmarkedArtistPostVO bookmarkedArtistPostVO);
}
