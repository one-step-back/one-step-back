package com.app.onestepback.controller.oAuth;

import com.app.onestepback.domain.vo.MemberVO;
import com.app.onestepback.service.etc.KaKaoService;
import com.app.onestepback.service.member.MemberService;
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
            MemberVO memberVO = memberService.findByEmail(foundInfo.get().getMemberEmail()).get();
            // 멤버스테이터스 검증 부분 수정해주셔야 합니다.
//            if ("DISABLE".equals(memberVO.getMemberStatus())) {
//                memberService.cancelWithdraw(memberVO.getId());
//                memberVO.setMemberStatus("ACTIVE");
//                redirectAttributes.addFlashAttribute("withdraw", false);
//            }
            session.setAttribute("member", memberVO);
            if (memberService.checkArtist(memberVO.getId()).isPresent()){
                session.setAttribute("artistId", memberVO.getId());
            }
            return new RedirectView("/");
        }
        return new RedirectView("/member/login");
    }
}
