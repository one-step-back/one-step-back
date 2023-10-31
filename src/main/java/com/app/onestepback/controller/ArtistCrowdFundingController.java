package com.app.onestepback.controller;

import com.app.onestepback.domain.CrowdFundingVO;
import com.app.onestepback.domain.FundingRequestVO;
import com.app.onestepback.domain.MemberVO;
import com.app.onestepback.domain.Pagination;
import com.app.onestepback.service.ArtistCrowdFundingService;
import com.app.onestepback.service.ArtistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/artist/crowd-funding/*")
public class ArtistCrowdFundingController {
    private final ArtistService artistService;
    private final ArtistCrowdFundingService artistCrowdFundingService;

    //    멤버 아이디로 크라우드 펀딩의 갯수 조회
    @GetMapping("getCFCount")
    public @ResponseBody int getCFCount(@RequestParam("memberId") Long memberId) {
        return artistCrowdFundingService.getCountOfCF(memberId);
    }

    //    크라우드 펀딩 목록으로 이동
    @GetMapping("list")
    public void goToList(@RequestParam("memberId") Long memberId, @RequestParam(value = "page", required = false) Integer page, Model model, Pagination pagination){
        model.addAttribute("artist", artistService.getArtist(memberId).get());
        pagination.setTotal(artistCrowdFundingService.getCountOfCF(memberId));
        pagination.setPage(page);
        pagination.setRowCount(5);
        pagination.progress();
        model.addAttribute("pagination", pagination);
        model.addAttribute("crowdFundings", artistCrowdFundingService.getAllCrowdFundings(memberId, pagination));
    }

    //    멤버 아이디로 펀딩 요청의 갯수 조회
    @GetMapping("getFRCount")
    public @ResponseBody int getFRCount(@RequestParam("memberId") Long memberId) {
        return artistCrowdFundingService.getCountOfFR(memberId);
    }

    //    펀딩 요청 목록 & 작성 으로 이동
    @GetMapping("request")
    public void goToRequest(FundingRequestVO fundingRequestVO, CrowdFundingVO crowdFundingVO, @RequestParam("memberId") Long memberId, @RequestParam(value = "page", required = false) Integer page, Model model, Pagination pagination){
        model.addAttribute("artist", artistService.getArtist(memberId).get());
        pagination.setTotal(artistCrowdFundingService.getCountOfFR(memberId));
        pagination.setPage(page);
        pagination.setRowCount(5);
        pagination.progress();
        model.addAttribute("pagination", pagination);
        model.addAttribute("crowdFundingRequests", artistCrowdFundingService.getAllRequests(memberId, pagination));
    }

    //    펀딩 요청 작성
    @PostMapping("request")
    // RequestParam
    public RedirectView request(FundingRequestVO fundingRequestVO, @RequestParam("memberId") Long memberId, HttpSession session){
        // 폼 태그로 submit을 통해 DTO에 작성된 펀딩 요청 정보가 자동으로 채워진 채 넘어옴
        // (아티스트)memberId와 (작성자)writerId만 담고 artistCrowdFundingService로 DTO 넘겨주기
        fundingRequestVO.setMemberId(memberId);
        fundingRequestVO.setWriterId(((MemberVO)session.getAttribute("member")).getId());
        artistCrowdFundingService.requestFunding(fundingRequestVO);
        return new RedirectView("/artist/crowd-funding/request?memberId=" + memberId);
    }

    //    펀딩 요청 수락
    @PostMapping("accept")
    public RedirectView accept(CrowdFundingVO crowdFundingVO){
        artistCrowdFundingService.acceptCrowdFundingRequest(crowdFundingVO);
        return new RedirectView("/artist/crowd-funding/request?memberId=" + crowdFundingVO.getMemberId());
    }

    //    펀딩 요청 거절
    @GetMapping("reject")
    public RedirectView reject(Long id, Long memberId){
        artistCrowdFundingService.rejectFundingRequest(id);
        return new RedirectView("/artist/crowd-funding/request?memberId=" + memberId);
    }

    //    펀딩 결제로 이동
    @GetMapping("pay")
    public void goToPay(){;}
}
