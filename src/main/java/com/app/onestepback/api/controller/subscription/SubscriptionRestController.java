package com.app.onestepback.api.controller.subscription;

import com.app.onestepback.domain.dto.SubscriptionDTO;
import com.app.onestepback.domain.vo.MemberVO;
import com.app.onestepback.domain.vo.SubscriptionVO;
import com.app.onestepback.service.artist.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscription")
public class SubscriptionRestController {
    private final SubscriptionService subscriptionService;

    @GetMapping("/subscribe")
    public boolean subscribe(@RequestParam("artistId")Long artistId,
                                     @RequestParam("status")boolean status,
                                     HttpSession session){
        MemberVO member = (MemberVO) session.getAttribute("member");

        return subscriptionService.doSubscribe(artistId, member.getId(), status);
    }

    @GetMapping("bring-all-artists")
    public List<SubscriptionDTO> getAllSubscribedArtist(@RequestParam("memberId")Long memberId){
        return subscriptionService.getAllSubscribedArtist(memberId);
    }

//    @GetMapping("get-one-post")
//    public Optional<CombinedPostDTO> getLatestOne(@RequestParam("memberId")Long memberId){
//        return subscriptionService.getLatestOne(memberId);
//    }
}
