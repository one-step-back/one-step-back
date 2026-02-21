package com.app.onestepback.web.controller.artist;

import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.global.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/artist/dashboard")
public class ArtistDashboardController {

    /**
     * 아티스트 대시보드 메인(요약) 페이지로 이동합니다.
     */
    @GetMapping
    public String goToDashboard(
            @LoginUser(artistOnly = true) SessionUser user,
            Model model
    ) {
        addCommonAttributes(model, user);
        model.addAttribute("dashboardTab", "summary");
        return "artist/dashboard/index";
    }

    /**
     * 정산 내역 관리 페이지로 이동합니다.
     */
    @GetMapping("/settlement")
    public String goToSettlementHistory(
            @LoginUser(artistOnly = true) SessionUser user,
            Model model
    ) {
        addCommonAttributes(model, user);
        model.addAttribute("dashboardTab", "settlement");
        return "artist/dashboard/settlement-history";
    }

    /**
     * 유료 멤버십 구독자 관리 페이지로 이동합니다.
     */
    @GetMapping("/subscribers")
    public String goToSubscribers(
            @LoginUser(artistOnly = true) SessionUser user,
            Model model
    ) {
        addCommonAttributes(model, user);
        model.addAttribute("dashboardTab", "subscribers");
        return "artist/dashboard/subscribers";
    }

    /**
     * 팔로워 관리 페이지로 이동합니다.
     */
    @GetMapping("/followers")
    public String goToFollowers(
            @LoginUser(artistOnly = true) SessionUser user,
            Model model
    ) {
        addCommonAttributes(model, user);
        model.addAttribute("dashboardTab", "followers");
        return "artist/dashboard/followers";
    }

    private void addCommonAttributes(Model model, SessionUser user) {
        model.addAttribute("artistId", user.getId());
        model.addAttribute("artistNickname", user.getMemberNickname());
    }
}