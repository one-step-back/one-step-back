package com.app.onestepback.service.etc;

import com.app.onestepback.domain.vo.BookmarkedArtistPostVO;
import com.app.onestepback.domain.vo.BookmarkedVideoVO;

import java.util.Optional;

public interface BookmarkService {
    public Optional<BookmarkedArtistPostVO> checkArtistPostBookmarkInfo(BookmarkedArtistPostVO bookmarkedArtistPostVO);

    public void doBookmarkArtistPost(BookmarkedArtistPostVO bookmarkedArtistPostVO);

    public void eraseBookmarkedArtistPost(BookmarkedArtistPostVO bookmarkedArtistPostVO);

    public Optional<BookmarkedVideoVO> checkVideoBookmarkInfo(BookmarkedVideoVO bookmarkedVideoVO);

    public void doBookmarkVideo(BookmarkedVideoVO bookmarkedVideoVO);

    public void eraseBookmarkedVideo(BookmarkedVideoVO bookmarkedVideoVO);
}
