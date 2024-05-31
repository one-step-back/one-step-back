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
    public boolean doBookmarkArtistPost(Long postId, Long memberId, boolean status) {
        if (!status) {
            bookmarkedArtistPostDAO.doBookmarkArtistPost(postId, memberId);
            return true;
        } else {
            bookmarkedArtistPostDAO.eraseBookmarkedArtistPost(postId, memberId);
            return false;
        }
    }

    @Override
    public boolean doBookmarkVideo(Long postId, Long memberId, boolean status) {
        if (!status) {
            bookmarkedVideoDAO.doBookmarkVideo(postId, memberId);
            return true;
        } else {
            bookmarkedVideoDAO.eraseBookmarkedVideo(postId, memberId);
            return false;
        }
    }
}
