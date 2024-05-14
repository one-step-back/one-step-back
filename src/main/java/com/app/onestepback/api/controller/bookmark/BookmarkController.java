package com.app.onestepback.api.controller.bookmark;

import com.app.onestepback.domain.entity.member.Member;
import com.app.onestepback.domain.vo.BookmarkedArtistPostVO;
import com.app.onestepback.domain.vo.BookmarkedVideoVO;
import com.app.onestepback.domain.vo.MemberVO;
import com.app.onestepback.service.etc.BookmarkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@Slf4j
@RequestMapping("/api/bookmark")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PatchMapping("/post")
    public boolean checkLibrary(@RequestParam("postId") Long postId,
                                @RequestParam("status") boolean status,
                                HttpSession session) {
        MemberVO member = (MemberVO) session.getAttribute("member");

        return bookmarkService.doBookmarkArtistPost(postId, member.getId(), status);
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
