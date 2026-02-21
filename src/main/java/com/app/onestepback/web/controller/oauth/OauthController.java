package com.app.onestepback.web.controller.oauth;

import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.global.config.KakaoConfig;
import com.app.onestepback.service.member.KakaoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OauthController {

    private final KakaoService kakaoService;
    private final KakaoConfig kakaoConfig;

    /**
     * 카카오 OAuth2 로그인을 처리하거나 카카오 인증 페이지로 리다이렉트합니다.
     */
    @GetMapping("/oauth2/kakao")
    public String kakaoConnect(
            @RequestParam(value = "code", required = false) String code,
            HttpSession session
    ) {
        if (code == null) {
            String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize"
                    + "?client_id=" + kakaoConfig.getClientId()
                    + "&redirect_uri=" + kakaoConfig.getRedirectUri()
                    + "&response_type=code";
            return "redirect:" + kakaoAuthUrl;
        }

        log.info("[OAuth2] 카카오 인가 코드 수신 완료. 로그인 처리를 시작합니다.");

        try {
            SessionUser sessionUser = kakaoService.login(code);
            session.setAttribute("member", sessionUser);

            log.info("[OAuth2] 카카오 로그인 성공 및 세션 생성: {}", sessionUser.getMemberEmail());
            return "redirect:/";

        } catch (Exception e) {
            log.error("[OAuth2] 카카오 연동 로그인 실패", e);
            return "redirect:/member/login-email?error=kakao";
        }
    }
}