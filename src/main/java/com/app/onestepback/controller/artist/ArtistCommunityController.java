package com.app.onestepback.controller.artist;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/artist/community/*")
public class ArtistCommunityController {

    @GetMapping("/list")
    public void goToListForm(){;}
}
