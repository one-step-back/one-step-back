package com.app.onestepback.controller;

import com.app.onestepback.domain.MemberVO;
import com.app.onestepback.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/member/*")
public class MemberController {
    private final MemberService memberService;

    // 회원가입 페이지로 이동
    @GetMapping("join")
    public void goToJoinForm(MemberVO memberVO){;}

    // 회원가입
    @PostMapping("join")
    public RedirectView join(MemberVO memberVO){
        memberService.join(memberVO, null);
        return new RedirectView("/member/login");
    }

    // 로그인 페이지로 이동
    @GetMapping("login")
    public void goToLoginForm(MemberVO memberVO) {
        ;
    }

    // 이메일 로그인 페이지로 이동
    @GetMapping("login-email")
    public void goToLoginEmailForm(MemberVO memberVO){;}
    @PostMapping("login-email")
    public RedirectView login(MemberVO memberVO, HttpSession session, RedirectAttributes redirectAttributes){
        Optional<MemberVO> foundMember = memberService.loginByEmail(memberVO);
        if(foundMember.isPresent()){
            session.setAttribute("member", foundMember.get());
            return new RedirectView("/my-page/my-page");
        }
        redirectAttributes.addFlashAttribute("login", "fail");
        return new RedirectView("/member/login-email");
    }

    // 로그아웃
    @GetMapping("logout")
    public RedirectView logout(HttpSession session) {
        session.invalidate();
        return new RedirectView("/member/login");
    }
}
