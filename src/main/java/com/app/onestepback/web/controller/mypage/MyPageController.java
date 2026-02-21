package com.app.onestepback.web.controller.mypage;

import com.app.onestepback.domain.dto.follow.FollowSearchCond;
import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.domain.dto.subscription.SubscriptionDTO;
import com.app.onestepback.global.annotation.LoginUser;
import com.app.onestepback.service.subscription.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/my-page")
public class MyPageController {

    private final SubscriptionService subscriptionService;

    /**
     * 마이페이지 진입 시 기본 탭인 프로필 설정 페이지로 리다이렉트합니다.
     */
    @GetMapping
    public String goToMyPageRoot() {
        return "redirect:/my-page/profile";
    }

    /**
     * 회원 프로필 설정 및 관리 페이지로 이동합니다.
     */
    @GetMapping("/profile")
    public String profile(@LoginUser SessionUser user, Model model) {
        model.addAttribute("tab", "profile");
        return "my-page/my-profile";
    }

    /**
     * 등록된 결제 수단 관리 페이지로 이동합니다.
     */
    @GetMapping("/payment")
    public String payment(@LoginUser SessionUser user, Model model) {
        model.addAttribute("tab", "payment");
        return "my-page/my-payment";
    }

    /**
     * 현재 가입 중인 유료 멤버십 구독 관리 페이지로 이동합니다.
     */
    @GetMapping("/subscription")
    public String subscription(@LoginUser SessionUser user, Model model) {
        model.addAttribute("tab", "subscription");
        List<SubscriptionDTO.Management> subscriptions = subscriptionService.findActiveSubscriptions(user.getId());

        model.addAttribute("subscriptions", subscriptions);
        return "my-page/my-subscription";
    }

    /**
     * 팔로우 중인 아티스트 목록 페이지로 이동합니다.
     */
    @GetMapping("/following")
    public String myFollowing(Model model) {
        model.addAttribute("tab", "following");
        model.addAttribute("sortOptions", FollowSearchCond.Sort.values());
        return "my-page/my-following";
    }

    /**
     * 후원한 크라우드 펀딩 내역 페이지로 이동합니다.
     */
    @GetMapping("/funding")
    public String goToFunding(@LoginUser SessionUser __, Model model) {
        model.addAttribute("tab", "funding");
        return "my-page/my-funding";
    }
}