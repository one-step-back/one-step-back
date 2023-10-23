package com.app.onestepback.controller;

import com.app.onestepback.domain.Pagination;
import com.app.onestepback.service.ArtistCrowdFundingService;
import com.app.onestepback.service.ArtistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/artist/crowd-funding/*")
public class ArtistCrowdFundingController {
    private final ArtistService artistService;
    private final ArtistCrowdFundingService artistCrowdFundingService;

    @GetMapping("list")
    public void goToListForm(@RequestParam("memberId") Long memberId, @RequestParam(value = "page", required = false) Integer page, Model model, Pagination pagination){
        model.addAttribute("artist", artistService.getArtist(memberId).get());
        pagination.setTotal(artistCrowdFundingService.getCountOfCF(memberId));
        pagination.setPage(page);
        pagination.setRowCount(5);
        pagination.progress();
        model.addAttribute("pagination", pagination);
        model.addAttribute("crowdFundings", artistCrowdFundingService.getAllCrowdFundings(memberId, pagination));
    }

    @GetMapping("getCFCount")
    public @ResponseBody int getCFCount(@RequestParam("memberId") Long memberId) {
        return artistCrowdFundingService.getCountOfCF(memberId);
    }

    @GetMapping("pay")
    public void goToPayForm(){;}

    @GetMapping("write")
    public void goToWriteForm(){;}
}
