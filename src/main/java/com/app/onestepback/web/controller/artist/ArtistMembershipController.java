package com.app.onestepback.web.controller.artist;

import com.app.onestepback.domain.dto.artist.ArtistPageDTO;
import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.domain.dto.membership.MembershipDTO;
import com.app.onestepback.domain.dto.membership.MembershipDetailDTO;
import com.app.onestepback.domain.dto.subscription.SubscriptionDTO;
import com.app.onestepback.global.annotation.LoginUser;
import com.app.onestepback.service.artist.ArtistService;
import com.app.onestepback.service.membership.MembershipService;
import com.app.onestepback.service.subscription.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/artist/{artistId}/membership")
public class ArtistMembershipController {

    private final ArtistService artistService;
    private final MembershipService membershipService;
    private final SubscriptionService subscriptionService;

    @ModelAttribute("artist")
    public ArtistPageDTO populateArtistDetail(
            @PathVariable("artistId") Long artistId,
            @LoginUser(required = false) SessionUser user
    ) {
        Long viewerId = (user != null) ? user.getId() : null;
        return artistService.getArtistDetail(artistId, viewerId);
    }

    /**
     * 아티스트의 멤버십 상품 목록 페이지로 이동합니다.
     */
    @GetMapping
    public String goToMembership(
            @LoginUser(required = false) SessionUser user,
            @PathVariable(name = "artistId") Long artistId,
            Model model
    ) {
        Long viewerId = (user != null) ? user.getId() : null;
        List<MembershipDTO> memberships = membershipService.getMemberships(artistId, viewerId);

        model.addAttribute("memberships", memberships);
        model.addAttribute("tab", "membership");

        return "artist/membership/index";
    }

    /**
     * 멤버십 가입 및 등급 변경을 위한 결제 확인 페이지로 이동합니다.
     */
    @GetMapping("/{membershipId}/confirm")
    public String goToMembershipConfirm(
            @LoginUser SessionUser user,
            Model model,
            @PathVariable("artistId") Long artistId,
            @PathVariable("membershipId") Long membershipId
    ) {
        MembershipDetailDTO targetMembership = membershipService.getMembershipInfo(membershipId);
        SubscriptionDTO.Info currentSub = subscriptionService.findActiveSubscription(user.getId(), artistId);

        String mode = "NEW";
        long paymentAmount = targetMembership.getPrice();

        if (currentSub != null) {
            if (currentSub.membershipId().equals(membershipId)) {
                mode = "SAME";
            } else if (targetMembership.getPrice() > currentSub.price()) {
                mode = "UPGRADE";
                paymentAmount = targetMembership.getPrice() - currentSub.price();
            } else {
                mode = "DOWNGRADE";
                paymentAmount = 0;
            }
            model.addAttribute("currentSub", currentSub);
        }

        model.addAttribute("membership", targetMembership);
        model.addAttribute("mode", mode);
        model.addAttribute("paymentAmount", paymentAmount);

        return "artist/membership/confirm";
    }

    /**
     * 멤버십 구독 결제 성공 영수증 페이지로 이동합니다.
     */
    @GetMapping("/success")
    public String goToSuccess(
            @PathVariable("artistId") Long artistId,
            @LoginUser SessionUser user,
            Model model
    ) {
        SubscriptionDTO.Receipt subscription = subscriptionService.getLatestSubscription(user.getId(), artistId);
        model.addAttribute("subscription", subscription);

        return "artist/membership/success";
    }
}