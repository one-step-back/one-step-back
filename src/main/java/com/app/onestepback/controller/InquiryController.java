package com.app.onestepback.controller;

import com.app.onestepback.domain.InquiryVO;
import com.app.onestepback.domain.MemberVO;
import com.app.onestepback.service.InquiryService;
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
@Slf4j
@RequestMapping("/inquiry/*")
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;
    @GetMapping("write")
    public void goToWriteForm(InquiryVO inquiryVO, HttpSession session, Model model){
        MemberVO memberVO = (MemberVO) session.getAttribute("member");
        model.addAttribute("memberId", memberVO.getId());
    }

    @PostMapping("write")
    public RedirectView saveInquiry(InquiryVO inquiryVO, HttpSession session){
        MemberVO foundMember = (MemberVO) session.getAttribute("member");

        inquiryVO.setMemberId(foundMember.getId());
        inquiryService.saveInquiry(inquiryVO);

        return new RedirectView("/");
    }
}
