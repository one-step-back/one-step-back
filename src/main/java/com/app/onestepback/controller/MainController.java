package com.app.onestepback.controller;

import com.app.onestepback.domain.dto.PostDTO;
import com.app.onestepback.domain.type.post.PostSortType;
import com.app.onestepback.service.MainService;
import com.app.onestepback.service.artist.ArtistPostService;
import com.app.onestepback.service.artist.VideoPostService;
import com.app.onestepback.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MainController {
    private final PostService postService;
    private final ArtistPostService artistPostService;
    private final VideoPostService videoPostService;

    // 메인 페이지
    @GetMapping("/")
    public String goToMainForm(Model model) {

        model.addAttribute("banners", postService.getPosts(5));
        model.addAttribute("posts", artistPostService.getArtistPosts(PostSortType.LIKE_DESC));
        model.addAttribute("videos", videoPostService.getVideos(PostSortType.VIEW_DESC));
//        model.addAttribute("crowdFundings", mainService.getCrowdfundingsForCards());
        return "main/index";
    }
}
