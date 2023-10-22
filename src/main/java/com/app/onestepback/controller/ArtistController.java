package com.app.onestepback.controller;

import com.app.onestepback.domain.ArtistDTO;
import com.app.onestepback.domain.ArtistPostDTO;
import com.app.onestepback.domain.SubscriptionVO;
import com.app.onestepback.domain.VideoDTO;
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
    public void goToMainForm(@RequestParam("id") Long id, Model model) {
        model.addAttribute("posts", artistService.get3Posts(id));
        model.addAttribute("videos", artistService.get3Videos(id));
        model.addAttribute("artist", artistService.getArtist(id).get());

        log.info(artistService.get3Posts(id).toString());
        log.info(artistService.get3Videos(id).toString());
    }

    @GetMapping("get-3videos")
    @ResponseBody
    public List<VideoDTO> load3Videos(@RequestParam("id") Long id) {
        return artistService.get3Videos(id);
    }

    @GetMapping("sponsor")
    public void goToSponsorForm() {
        ;
    }

    @GetMapping("/post/edit")
    public void goToPostEditForm() {
        ;
    }
}
