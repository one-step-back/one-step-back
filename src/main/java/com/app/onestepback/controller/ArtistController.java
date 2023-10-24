package com.app.onestepback.controller;

import com.app.onestepback.domain.ArtistDTO;
import com.app.onestepback.domain.ArtistPostDTO;
import com.app.onestepback.domain.SubscriptionVO;
import com.app.onestepback.domain.VideoPostDTO;
import com.app.onestepback.repository.ArtistPostDAO;
import com.app.onestepback.service.ArtistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("/artist/*")
@RequiredArgsConstructor
public class ArtistController {
    private final ArtistService artistService;

    @GetMapping("main")
    public void goToMainForm(@RequestParam("memberId") Long memberId, Model model) {
        model.addAttribute("artist", artistService.getArtist(memberId).get());
        model.addAttribute("posts", artistService.get3Posts(memberId));
        model.addAttribute("videos", artistService.get3Videos(memberId));
    }
}
