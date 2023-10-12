package com.app.onestepback.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/search/*")
public class SearchController {

    @GetMapping("artists")
    public void goToArtistsForm(){;}

    @GetMapping("integrated-search")
    public void goToIntegratedSearchForm(){;}

    @GetMapping("videos")
    public void goToVideosForm(){;}
}
