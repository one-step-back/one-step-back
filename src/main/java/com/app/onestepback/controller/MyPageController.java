package com.app.onestepback.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/my-page/*")
public class MyPageController {

    @GetMapping("email")
    public void goToEmailForm(){;}

    @GetMapping("my-page")
    public void goToMyPageForm(){;}

    @GetMapping("password")
    public void goToPasswordForm(){;}

    @GetMapping("profile")
    public void goToProfileForm(){;}
}
