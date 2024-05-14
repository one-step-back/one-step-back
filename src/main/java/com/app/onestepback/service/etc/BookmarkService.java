package com.app.onestepback.service.etc;

import com.app.onestepback.domain.vo.BookmarkedArtistPostVO;
import com.app.onestepback.domain.vo.BookmarkedVideoVO;

import java.util.Optional;

public interface BookmarkService {
    Optional<BookmarkedArtistPostVO> checkArtistPostBookmarkInfo(BookmarkedArtistPostVO bookmarkedArtistPostVO);

    boolean doBookmarkArtistPost(Long postId, Long memberId, boolean status);

    public Optional<BookmarkedVideoVO> checkVideoBookmarkInfo(BookmarkedVideoVO bookmarkedVideoVO);

    public void doBookmarkVideo(BookmarkedVideoVO bookmarkedVideoVO);

    public void eraseBookmarkedVideo(BookmarkedVideoVO bookmarkedVideoVO);
}
