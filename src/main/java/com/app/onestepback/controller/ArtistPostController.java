package com.app.onestepback.controller;

import com.app.onestepback.domain.ArtistPostDTO;
import com.app.onestepback.domain.Pagination;
import com.app.onestepback.service.ArtistPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/artist/post/*")
public class ArtistPostController {
    private final ArtistPostService artistPostService;

    @GetMapping("list")
    public void goToPostList(Pagination pagination, @RequestParam("id") Long id, @RequestParam(value = "page", required = false) Integer page, Model model) {
        model.addAttribute("artist", artistPostService.getArtist(id).get());

        pagination.setTotal(artistPostService.getPostCount(id));
        pagination.setPage(page);
        pagination.progress();
        model.addAttribute("pagination", pagination);
        model.addAttribute("posts", artistPostService.getAllPosts(id, pagination));
    }

    @GetMapping("write")
    public void goToPostWriteForm() {
        ;
    }
}
