package com.app.onestepback.api.controller;

import com.app.onestepback.domain.dto.CombinedPostDTO;
import com.app.onestepback.domain.dto.SubscriptionDTO;
import com.app.onestepback.domain.vo.SubscriptionVO;
import com.app.onestepback.service.artist.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscription/*")
public class SubscriptionRestController {
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

    @GetMapping("bring-all-artists")
    public List<SubscriptionDTO> getAllSubscribedArtist(@RequestParam("memberId")Long memberId){
        return subscriptionService.getAllSubscribedArtist(memberId);
    }

    @GetMapping("get-one-post")
    public Optional<CombinedPostDTO> getLatestOne(@RequestParam("memberId")Long memberId){
        return subscriptionService.getLatestOne(memberId);
    }
}
