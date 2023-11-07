package com.app.onestepback.controller;

import com.app.onestepback.domain.Pagination;
import com.app.onestepback.service.ArtistCommunityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/artist/community/*")
public class ArtistCommunityController {
    private final ArtistCommunityService artistCommunityService;

    @GetMapping("/list")
    public void goToListForm (Pagination pagination, @RequestParam("memberId") Long memberId,@RequestParam(value = "page", required = false) Integer page, Model model){
        model.addAttribute("community", artistCommunityService.getCommunityCount());

        pagination.setTotal(artistCommunityService.getCommunityCount());
        pagination.setPage(page);
        pagination.setRowCount(10);
        pagination.progress();
        model.addAttribute("pagination", pagination);
        model.addAttribute("posts", artistCommunityService.getselectAllCommunity(memberId, pagination));

    }

}
