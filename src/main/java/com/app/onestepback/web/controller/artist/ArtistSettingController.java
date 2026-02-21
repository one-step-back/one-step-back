package com.app.onestepback.web.controller.artist;

import com.app.onestepback.domain.dto.artist.ArtistPageDTO;
import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.global.annotation.LoginUser;
import com.app.onestepback.service.artist.ArtistService;
import com.app.onestepback.web.api.v1.artist.request.ArtistUpdateRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/artist/setting")
public class ArtistSettingController {

    private final ArtistService artistService;

    /**
     * 아티스트 프로필 및 설정 관리 페이지로 이동합니다.
     */
    @GetMapping
    public String goToSettingPage(
            @LoginUser SessionUser user,
            Model model
    ) {
        ArtistPageDTO artist = artistService.getArtistDetail(user.getId(), user.getId());
        model.addAttribute("artist", artist);

        ArtistUpdateRequest request = new ArtistUpdateRequest(
                artist.getProfile().getBlogName(),
                artist.getProfile().getDescription(),
                null,
                artist.getProfile().getNickname(),
                null
        );
        model.addAttribute("artistUpdateRequest", request);

        return "artist/setting/index";
    }

    /**
     * 아티스트의 프로필 및 설정 정보를 수정하고 세션을 갱신합니다.
     */
    @PostMapping("/update")
    public String updateArtist(
            @LoginUser SessionUser user,
            @Valid @ModelAttribute("artistUpdateRequest") ArtistUpdateRequest body,
            BindingResult bindingResult,
            Model model,
            HttpSession session
    ) {
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors: {}", bindingResult.getAllErrors());

            ArtistPageDTO artist = artistService.getArtistDetail(user.getId(), user.getId());
            model.addAttribute("artist", artist);

            return "artist/setting/index";
        }

        SessionUser updatedSessionUser = artistService.updateArtist(body.toCmd(user.getId()));
        session.setAttribute("member", updatedSessionUser);

        return "redirect:/artist/" + user.getId();
    }
}