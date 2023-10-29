package com.app.onestepback.controller;

import com.app.onestepback.domain.PostLikeVO;
import com.app.onestepback.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like/*")
public class PostLikeController {
    private final PostLikeService postLikeService;

    @GetMapping("check-like")
    public boolean checkLike(@RequestParam("postId") Long postId, @RequestParam("memberId") Long memberId){
        PostLikeVO postLikeVO = new PostLikeVO();
        postLikeVO.setPostId(postId);
        postLikeVO.setMemberId(memberId);

        return postLikeService.checkPostLike(postLikeVO).isPresent();
    }

    @PostMapping("update-like")
    public boolean saveLike(@RequestParam("postId") Long postId, @RequestParam("memberId") Long memberId, @RequestParam("likeStatus") boolean likeStatus){
        PostLikeVO postLikeVO = new PostLikeVO();
        postLikeVO.setPostId(postId);
        postLikeVO.setMemberId(memberId);

        if (!likeStatus){
            postLikeService.saveLike(postLikeVO);
            return true;
        } else {
            postLikeService.eraseLike(postLikeVO);
            return false;
        }
    }
}
