package com.app.onestepback.controller;

import com.app.onestepback.domain.SubscriptionVO;
import com.app.onestepback.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscription/*")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @GetMapping("check-subscription")
    public boolean checkSubscription(@RequestParam("artistId")Long artistId, @RequestParam("memberId")Long memberId){
        SubscriptionVO subscriptionVO = new SubscriptionVO();
        subscriptionVO.setArtistId(artistId);
        subscriptionVO.setMemberId(memberId);

        if (subscriptionService.checkSubscription(subscriptionVO).isEmpty()){
            return false;
        } else {
            return true;
        }
    }

    @GetMapping("update-subscription")
    public boolean updateSubscription(@RequestParam("subscriptionStatus")boolean subscriptionStatus, @RequestParam("artistId")Long artistId, @RequestParam("memberId")Long memberId){
        SubscriptionVO subscriptionVO = new SubscriptionVO();
        subscriptionVO.setArtistId(artistId);
        subscriptionVO.setMemberId(memberId);

        if(!subscriptionStatus){
            subscriptionService.saveSubscription(subscriptionVO);
            return true;
        } else {
            subscriptionService.cancelSubscription(subscriptionVO);
            return false;
        }
    }
}
