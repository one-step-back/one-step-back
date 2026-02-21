package com.app.onestepback.web.controller.member;

import com.app.onestepback.web.api.v1.member.request.LoginWithEmailPasswordRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    /**
     * 자체 이메일 회원가입 페이지로 이동합니다.
     */
    @GetMapping("/join")
    public String goToJoinForm() {
        return "member/join";
    }

    /**
     * 통합 로그인(소셜 및 이메일) 선택 페이지로 이동합니다.
     */
    @GetMapping("/login")
    public String goToLoginForm() {
        return "member/login";
    }

    /**
     * 자체 이메일 로그인 폼 페이지로 이동합니다.
     */
    @GetMapping("/login-email")
    public String goToLoginEmailForm(
            @ModelAttribute("loginWithEmailPasswordRequest") LoginWithEmailPasswordRequest body
    ) {
        return "member/login-email";
    }

    /**
     * 현재 세션을 파기하고 시스템에서 로그아웃한 뒤 메인 페이지로 리다이렉트합니다.
     */
    @GetMapping("/logout")
    public RedirectView logout(HttpSession session) {
        session.invalidate();
        return new RedirectView("/");
    }
}