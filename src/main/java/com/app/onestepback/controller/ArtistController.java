package com.app.onestepback.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/artist/*")
public class ArtistController {

    @GetMapping("main")
    public void goToMainForm(){;}

    @GetMapping("sponsor")
    public void goToSponsorForm(){;}

    @GetMapping("/post/write")
    public void goToPostWriteForm(){;}

    @GetMapping("/post/edit")
    public void goToPostEditForm(){;}

    @GetMapping("/post/detail")
    public void goToPostDetailForm(){;}
}
