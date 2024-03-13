package com.app.onestepback.controller.mypage;

import com.app.onestepback.domain.vo.MemberVO;
import com.app.onestepback.service.mypage.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/my-page/*")
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping("email")
    public void goToEmailForm(){;}

    @GetMapping("my-page")
    public void goToMyPageForm(){;}

    @GetMapping("password")
    public void goToPasswordForm(){;}

    @GetMapping("profile")
    public void goToProfileForm(HttpSession session, Model model){
//        조회
        MemberVO memberVO = (MemberVO)session.getAttribute("member");
        model.addAttribute("member", myPageService.findById(memberVO.getId()));
    }

    @PostMapping("/profile")
    public RedirectView modifyProfile(MemberVO memberVO, HttpSession session){
//        수정
        MemberVO sessionMember = (MemberVO)session.getAttribute("member");
        memberVO.setId(sessionMember.getId());
        myPageService.modifyByNicknameIntroduction(memberVO);

        return new RedirectView("/my-page/profile");
    }
}
