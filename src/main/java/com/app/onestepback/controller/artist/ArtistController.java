package com.app.onestepback.controller.artist;

import com.app.onestepback.exception.CustomException;
import com.app.onestepback.service.artist.ArtistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequestMapping("/artist/*")
@RequiredArgsConstructor
public class ArtistController {
    private final ArtistService artistService;

    @GetMapping("main")
    public void goToMainForm(@RequestParam("memberId") Long memberId, Model model) {
        if (memberId == null){
            throw new CustomException("존재하지 않는 아티스트");
        }
        model.addAttribute("artist", artistService.getArtist(memberId).get());
        model.addAttribute("posts", artistService.get3Posts(memberId));
        model.addAttribute("videos", artistService.get3Videos(memberId));
    }

    @GetMapping("sponsor")
    public void goToSponsorPage(@RequestParam("memberId") Long memberId, Model model) {
        model.addAttribute("artist", artistService.getArtist(memberId).get());
    }
}
