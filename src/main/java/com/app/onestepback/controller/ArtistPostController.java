package com.app.onestepback.controller;

import com.app.onestepback.domain.ArtistPostDTO;
import com.app.onestepback.domain.Pagination;
import com.app.onestepback.service.ArtistPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/artist/post/*")
public class ArtistPostController {
    private final ArtistPostService artistPostService;

    @GetMapping("list")
    public void goToPostList(@RequestParam("id") Long id, Model model) {
        model.addAttribute("artist", artistPostService.getArtist(id).get());
    }

    @GetMapping("get-posts")
    @ResponseBody
    public List<ArtistPostDTO> loadPostsByPagination(Pagination pagination, @RequestParam("id")Long id, @RequestParam("page")int page){
        pagination.setTotal(artistPostService.getPostCount(id));
        pagination.setPage(page);
        pagination.progress();
        return artistPostService.getAllPosts(id, pagination);
    }
}
