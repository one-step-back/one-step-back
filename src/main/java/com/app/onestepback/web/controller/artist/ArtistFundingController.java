package com.app.onestepback.web.controller.artist;

import com.app.onestepback.domain.dto.artist.ArtistPageDTO;
import com.app.onestepback.domain.dto.crowdfunding.CrowdFundingDTO;
import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.domain.type.crowdfunding.CrowdFundingStatus;
import com.app.onestepback.global.annotation.LoginUser;
import com.app.onestepback.global.exception.BusinessException;
import com.app.onestepback.global.exception.ErrorCode;
import com.app.onestepback.service.artist.ArtistService;
import com.app.onestepback.service.crowdfunding.CrowdFundingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/artist/{artistId}/funding")
public class ArtistFundingController {

    private final ArtistService artistService;
    private final CrowdFundingService crowdFundingService;

    @ModelAttribute("artist")
    public ArtistPageDTO populateArtistDetail(
            @PathVariable("artistId") Long artistId,
            @LoginUser(required = false) SessionUser user
    ) {
        Long viewerId = (user != null) ? user.getId() : null;
        return artistService.getArtistDetail(artistId, viewerId);
    }

    /**
     * 아티스트의 펀딩 목록 페이지로 이동합니다.
     */
    @GetMapping
    public String index(Model model) {
        model.addAttribute("tab", "funding");
        return "artist/funding/index";
    }

    /**
     * 펀딩 프로젝트 제안 및 작성 페이지로 이동합니다.
     */
    @GetMapping("/write")
    public String goToWrite(@LoginUser SessionUser __) {
        return "artist/funding/write";
    }

    /**
     * 특정 펀딩 프로젝트의 상세 페이지로 이동합니다.
     */
    @GetMapping("/{fundingId}")
    public String detail(
            @PathVariable("fundingId") Long fundingId,
            Model model
    ) {
        CrowdFundingDTO.Detail funding = crowdFundingService.getDetail(fundingId);
        model.addAttribute("funding", funding);
        return "artist/funding/detail";
    }

    /**
     * 펀딩 후원 결제 확인 페이지로 이동합니다.
     */
    @GetMapping("/{fundingId}/payment")
    public String paymentConfirm(
            @PathVariable("fundingId") Long fundingId,
            @RequestParam("amount") Long amount,
            @LoginUser SessionUser user,
            Model model
    ) {
        if (amount < 1000) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        CrowdFundingDTO.PaymentView paymentInfo = crowdFundingService.getPaymentInfo(fundingId, user.getId());

        model.addAttribute("funding", paymentInfo);
        model.addAttribute("amount", amount);
        model.addAttribute("hasPaymentMethod", user.isHasPaymentMethod());

        return "artist/funding/payment";
    }

    /**
     * 펀딩 후원 결제 성공 영수증 페이지로 이동합니다.
     */
    @GetMapping("/{fundingId}/success")
    public String goToSuccess(
            @LoginUser SessionUser user,
            @RequestParam(name = "payment-id") Long paymentId,
            @PathVariable("fundingId") Long fundingId,
            Model model
    ) {
        CrowdFundingDTO.Receipt receipt = crowdFundingService.getPaymentReceipt(fundingId, paymentId, user.getId());
        model.addAttribute("receipt", receipt);

        return "artist/funding/success";
    }

    /**
     * 제안받은 펀딩의 승낙을 위한 수정 페이지로 이동합니다.
     */
    @GetMapping("/{fundingId}/edit")
    public String goToEdit(
            @PathVariable("fundingId") Long fundingId,
            @LoginUser(artistOnly = true) SessionUser user,
            Model model
    ) {
        CrowdFundingDTO.Detail funding = crowdFundingService.getDetail(fundingId);

        if (!funding.artistId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        if (funding.status() != CrowdFundingStatus.WAITING) {
            throw new BusinessException(ErrorCode.CROWD_FUNDING_NOT_WAITING);
        }

        model.addAttribute("funding", funding);
        return "artist/funding/edit";
    }
}