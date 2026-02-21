package com.app.onestepback.web.controller.artist;

import com.app.onestepback.domain.dto.artist.ArtistPageDTO;
import com.app.onestepback.domain.dto.feed.FeedDetailDTO;
import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.domain.type.feed.FeedCategory;
import com.app.onestepback.domain.type.feed.FeedStatus;
import com.app.onestepback.global.annotation.LoginUser;
import com.app.onestepback.service.artist.ArtistService;
import com.app.onestepback.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/artist/{artistId}/feed")
@RequiredArgsConstructor
public class ArtistFeedController {

    private final ArtistService artistService;
    private final FeedService feedService;

    @ModelAttribute("artist")
    public ArtistPageDTO populateArtistDetail(
            @PathVariable("artistId") Long artistId,
            @LoginUser(required = false) SessionUser user
    ) {
        Long viewerId = (user != null) ? user.getId() : null;
        return artistService.getArtistDetail(artistId, viewerId);
    }

    @ModelAttribute("categories")
    public FeedCategory[] categories() {
        return FeedCategory.values();
    }

    @ModelAttribute("statuses")
    public FeedStatus[] statuses() {
        return FeedStatus.values();
    }

    /**
     * 아티스트의 피드 목록 페이지로 이동합니다.
     */
    @GetMapping
    public String feed(Model model) {
        model.addAttribute("tab", "feed");
        return "artist/feed/index";
    }

    /**
     * 피드 작성 및 수정 페이지로 이동합니다.
     */
    @GetMapping("/write")
    public String goToWrite(
            @PathVariable("artistId") Long artistId,
            @RequestParam(value = "id", required = false) Long id,
            @LoginUser(artistOnly = true) SessionUser user,
            Model model
    ) {
        if (user == null || !user.getId().equals(artistId)) {
            return "redirect:/artist/" + artistId + "/feed";
        }

        if (id != null) {
            FeedDetailDTO feed = feedService.getForEdit(id, user.getId());
            model.addAttribute("feed", feed);
            model.addAttribute("mode", "edit");
        } else {
            model.addAttribute("mode", "write");
        }

        return "artist/feed/write";
    }

    /**
     * 특정 피드의 상세 열람 페이지로 이동합니다.
     */
    @GetMapping("/view")
    public String goToView(
            @PathVariable("artistId") Long artistId,
            @LoginUser(required = false) SessionUser user,
            @RequestParam("id") Long feedId,
            Model model
    ) {
        Long viewerId = (user != null) ? user.getId() : null;
        FeedDetailDTO feed = feedService.getDetail(feedId, artistId, viewerId);

        model.addAttribute("feed", feed);
        return "artist/feed/view";
    }
}