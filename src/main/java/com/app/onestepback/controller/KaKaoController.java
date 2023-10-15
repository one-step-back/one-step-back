package com.app.onestepback.controller;

import com.app.onestepback.domain.MemberVO;
import com.app.onestepback.service.KaKaoService;
import com.app.onestepback.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class KaKaoController {
    private final KaKaoService kaKaoService;
    private final MemberService memberService;

    @GetMapping("/kakao/login")
    public RedirectView login(String code, HttpSession session, RedirectAttributes redirectAttributes){
        Long id = null;
        String token = kaKaoService.getKaKaoAccessToken(code);
        Optional<MemberVO> foundInfo = kaKaoService.getKaKaoInfo(token);
        if(foundInfo.isPresent()){
            if(session.getAttribute("member") != null){
                id = ((MemberVO)session.getAttribute("member")).getId();
            }
            memberService.join(foundInfo.get(), id);
//          여기 지금 findByEmail().get() 이 null 이라고 함 -> 이메일로 검색이 안 됨, join이 제대로 안 됐음.
            MemberVO memberVO = memberService.findByEmail(foundInfo.get().getMemberEmail()).get();
            if ("DISABLE".equals(memberVO.getMemberStatus())) {
                memberService.cancelWithdraw(memberVO.getId());
                memberVO.setMemberStatus("ACTIVE");
                redirectAttributes.addFlashAttribute("withdraw", false);
            }
            session.setAttribute("member", memberVO);
            return new RedirectView("/my-page/my-page");
        }
        return new RedirectView("/member/login");
    }
}
