package com.app.onestepback.controller.mypage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/my-library/*")
public class MyLibraryController {

    @GetMapping("posts")
    public void goToPosts(){;}

    @GetMapping("videos")
    public void goToVideos(){;}
}
