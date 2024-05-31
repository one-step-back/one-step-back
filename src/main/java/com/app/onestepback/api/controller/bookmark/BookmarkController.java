package com.app.onestepback.api.controller.bookmark;

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
    public boolean bookmarkPost(@RequestParam("postId") Long postId,
                                @RequestParam("status") boolean status,
                                HttpSession session) {
        MemberVO member = (MemberVO) session.getAttribute("member");

        return bookmarkService.doBookmarkArtistPost(postId, member.getId(), status);
    }

    @PatchMapping("/video")
    public boolean bookmarkVideo(@RequestParam("postId") Long postId,
                                 @RequestParam("status") boolean status,
                                 HttpSession session) {
        MemberVO member = (MemberVO) session.getAttribute("member");

        return bookmarkService.doBookmarkVideo(postId, member.getId(), status);
    }
}
