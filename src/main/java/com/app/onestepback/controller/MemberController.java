package com.app.onestepback.controller;

import com.app.onestepback.domain.MemberVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/member/*")
public class MemberController {

    // 로그인 페이지로 이동
    @GetMapping("login")
    public void goToLoginForm(MemberVO memberVO) {
        ;
    }

    // 이메일 로그인 페이지로 이동
    @GetMapping("login-email")
    public void goToLoginEmailForm(MemberVO memberVO){;}

    // 회원가입 페이지로 이동
    @GetMapping("join")
    public void goToJoinForm(MemberVO memberVO){;}

    @PostMapping("join")
    public RedirectView join(MemberVO memberVO, HttpSession session){

    }

    // 로그아웃
    @GetMapping("logout")
    public RedirectView logout(HttpSession session) {
        session.invalidate();
        return new RedirectView("/member/login");
    }

}
