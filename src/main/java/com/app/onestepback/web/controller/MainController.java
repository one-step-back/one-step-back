package com.app.onestepback.web.controller;

import com.app.onestepback.domain.dto.artist.ArtistDTO;
import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.domain.type.artist.ArtistSortType;
import com.app.onestepback.global.annotation.LoginUser;
import com.app.onestepback.service.artist.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ArtistService artistService;

    /**
     * 애플리케이션의 메인 페이지를 렌더링하며 인기/신규 아티스트 통계 데이터를 주입합니다.
     */
    @GetMapping("/")
    public String goToMain(@LoginUser(required = false) SessionUser user, Model model) {
        Long viewerId = user != null ? user.getId() : null;

        List<ArtistDTO.ListInfo> popularArtists = artistService.getArtistList(viewerId, ArtistSortType.POPULAR)
                .stream().limit(4).collect(Collectors.toList());

        List<ArtistDTO.ListInfo> newArtists = artistService.getArtistList(viewerId, ArtistSortType.NEW)
                .stream().limit(4).collect(Collectors.toList());

        model.addAttribute("popularArtists", popularArtists);
        model.addAttribute("newArtists", newArtists);

        return "index";
    }

    /**
     * 등록된 전체 아티스트 목록 페이지를 렌더링합니다.
     */
    @GetMapping("/artist/list")
    public String list(
            @RequestParam(required = false) ArtistSortType sort,
            @LoginUser(required = false) SessionUser user,
            Model model
    ) {
        Long viewerId = user == null ? null : user.getId();
        List<ArtistDTO.ListInfo> artists = artistService.getArtistList(viewerId, sort);

        model.addAttribute("artists", artists);
        model.addAttribute("sort", sort);

        return "artist/list";
    }

    /**
     * 전체 크라우드 펀딩 프로젝트 목록 페이지를 렌더링합니다.
     */
    @GetMapping("/funding/list")
    public String list() {
        return "funding/list";
    }
}