package com.app.onestepback.web.controller.artist;

import com.app.onestepback.domain.dto.artist.ArtistDetailDTO;
import com.app.onestepback.domain.dto.artist.ArtistPageDTO;
import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.global.annotation.LoginUser;
import com.app.onestepback.service.artist.ArtistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/artist/{artistId}")
public class ArtistBlogController {

    private final ArtistService artistService;

    /**
     * 현재 블로그의 아티스트 상세 정보를 조회하여 모든 뷰에 공통으로 주입합니다.
     */
    @ModelAttribute("artist")
    public ArtistPageDTO populateArtistDetail(
            @PathVariable("artistId") Long artistId,
            @LoginUser(required = false) SessionUser user
    ) {
        Long viewerId = (user != null) ? user.getId() : null;
        return artistService.getArtistDetail(artistId, viewerId);
    }

    /**
     * 아티스트 블로그의 홈 화면으로 이동합니다.
     */
    @GetMapping
    public String home(Model model) {
        model.addAttribute("tab", "home");
        return "artist/home";
    }
}