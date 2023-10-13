package com.app.onestepback.controller;

import com.app.onestepback.domain.ArtistDTO;
import com.app.onestepback.service.ArtistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("/artist/*")
@RequiredArgsConstructor
public class ArtistController {
    private final ArtistService artistService;

    @GetMapping("main")
    public void goToMainForm(@RequestParam("id") Long id, Model model) {
        model.addAttribute("artist", artistService.getArtist(id).get());
    }

    @GetMapping("sponsor")
    public void goToSponsorForm() {
        ;
    }

    @GetMapping("/post/write")
    public void goToPostWriteForm() {
        ;
    }

    @GetMapping("/post/edit")
    public void goToPostEditForm() {
        ;
    }

    @GetMapping("/post/detail")
    public void goToPostDetailForm() {
        ;
    }
}
