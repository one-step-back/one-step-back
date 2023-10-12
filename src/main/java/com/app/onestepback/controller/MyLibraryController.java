package com.app.onestepback.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/my-library/*")
public class MyLibraryController {

    @GetMapping("posts")
    public void goToPostForm(){;}

    @GetMapping("videos")
    public void goToVideosForm(){;}

}
