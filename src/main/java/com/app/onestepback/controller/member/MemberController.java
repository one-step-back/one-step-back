package com.app.onestepback.controller.member;

import com.app.onestepback.domain.vo.MemberVO;
import com.app.onestepback.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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

    //    아이디 중복검사
    @GetMapping("check-email/{memberEmail}")
    @ResponseBody
    public boolean checkId(@PathVariable String memberEmail){
        return memberService.findByEmail(memberEmail).isPresent();
    }

    // 회원가입 페이지로 이동
    @GetMapping("join")
    public String goToJoinForm(MemberVO memberVO){
        return "member/join";
    }

    // 회원가입
    @PostMapping("join")
    public RedirectView join(MemberVO memberVO){
        memberService.join(memberVO, null);
        return new RedirectView("/member/login");
    }

    // 로그인 페이지로 이동
    @GetMapping("login")
    public String goToLoginForm(MemberVO memberVO) {
        return "member/login";
    }

    // 이메일 로그인 페이지로 이동
    @GetMapping("login-email")
    public String goToLoginEmailForm(MemberVO memberVO){
        return "member/login-email";
    }
    @PostMapping("login-email")
    public RedirectView login(MemberVO memberVO, HttpSession session, RedirectAttributes redirectAttributes){
        log.info(memberVO.toString());
        Optional<MemberVO> foundMember = memberService.loginByEmail(memberVO);
        if(foundMember.isPresent()){
            session.setAttribute("member", foundMember.get());
            if (memberService.checkArtist(foundMember.get().getId()).isPresent()){
                session.setAttribute("artistId", foundMember.get().getId());
            }
            return new RedirectView("/");
        }
        redirectAttributes.addFlashAttribute("login", "fail");
        return new RedirectView("/member/login-email");
    }

    // 로그아웃
    @GetMapping("logout")
    public RedirectView logout(HttpSession session) {
        session.invalidate();
        return new RedirectView("/");
    }
}
