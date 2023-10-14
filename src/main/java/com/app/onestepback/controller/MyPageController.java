package com.app.onestepback.controller;

import com.app.onestepback.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/my-page/*")
public class MyPageController {

    private final MemberService memberService;

    @GetMapping("email")
    public void goToEmailForm(){;}

    @GetMapping("my-page")
    public void goToMyPageForm(){;}

    @GetMapping("password")
    public void goToPasswordForm(){;}

    @GetMapping("profile")
    public void goToProfileForm(HttpSession session, Model model){

        model.addAttribute("member", memberService.bringMemberInfo(1L));

    }
}
