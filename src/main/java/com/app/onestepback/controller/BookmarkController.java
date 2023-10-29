package com.app.onestepback.controller;

import com.app.onestepback.domain.BookmarkedArtistPostVO;
import com.app.onestepback.domain.BookmarkedVideoVO;
import com.app.onestepback.domain.LibraryVO;
import com.app.onestepback.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/bookmark/*")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @GetMapping("check-post")
    public boolean checkLibrary(@RequestParam("postId") Long postId, @RequestParam("memberId") Long memberId) {
        BookmarkedArtistPostVO bookmarkedArtistPostVO = new BookmarkedArtistPostVO();
        bookmarkedArtistPostVO.setPostId(postId);
        bookmarkedArtistPostVO.setMemberId(memberId);

        return bookmarkService.checkArtistPostBookmarkInfo(bookmarkedArtistPostVO).isPresent();
    }

    @PostMapping("update-post")
    public boolean saveToLibrary(@RequestParam("postId") Long postId, @RequestParam("memberId") Long memberId, @RequestParam("bookmarkStatus") boolean bookmarkStatus) {
        BookmarkedArtistPostVO bookmarkedArtistPostVO = new BookmarkedArtistPostVO();
        bookmarkedArtistPostVO.setPostId(postId);
        bookmarkedArtistPostVO.setMemberId(memberId);

        if (!bookmarkStatus) {
            bookmarkService.doBookmarkArtistPost(bookmarkedArtistPostVO);
            return true;
        } else {
            bookmarkService.eraseBookmarkedArtistPost(bookmarkedArtistPostVO);
            return false;
        }
    }

    @GetMapping("check-video")
    public boolean checkVideoLibrary(@RequestParam("postId") Long postId, @RequestParam("memberId") Long memberId) {
        BookmarkedVideoVO bookmarkedVideoVO = new BookmarkedVideoVO();
        bookmarkedVideoVO.setPostId(postId);
        bookmarkedVideoVO.setMemberId(memberId);

        return bookmarkService.checkVideoBookmarkInfo(bookmarkedVideoVO).isPresent();
    }

    @PostMapping("update-video")
    public boolean saveToVideoLibrary(@RequestParam("postId") Long postId, @RequestParam("memberId") Long memberId, @RequestParam("bookmarkStatus") boolean bookmarkStatus) {
        BookmarkedVideoVO bookmarkedVideoVO = new BookmarkedVideoVO();
        bookmarkedVideoVO.setPostId(postId);
        bookmarkedVideoVO.setMemberId(memberId);

        if (!bookmarkStatus){
            bookmarkService.doBookmarkVideo(bookmarkedVideoVO);
            return true;
        } else {
            bookmarkService.eraseBookmarkedVideo(bookmarkedVideoVO);
            return false;
        }
    }
}
