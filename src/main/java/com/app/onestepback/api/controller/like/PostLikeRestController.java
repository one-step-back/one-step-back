package com.app.onestepback.api.controller.like;

import com.app.onestepback.domain.vo.MemberVO;
import com.app.onestepback.domain.vo.PostLikeVO;
import com.app.onestepback.service.file.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/like")
public class PostLikeRestController {
    private final PostLikeService postLikeService;

    @PatchMapping("/post")
    public boolean checkLike(@RequestParam("postId") Long postId,
                             @RequestParam("status") boolean status, HttpSession session) {
        MemberVO member = (MemberVO) session.getAttribute("member");

        return postLikeService.likePost(postId, status, member.getId());
    }

}
