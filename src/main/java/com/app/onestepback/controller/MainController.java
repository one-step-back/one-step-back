package com.app.onestepback.controller;

import com.app.onestepback.domain.CombinedPostDTO;
import com.app.onestepback.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MainController {
    private final MainService mainService;

    // 메인 페이지
    @GetMapping("/")
    public String goToMainForm(Model model) {

        model.addAttribute("bannerPosts", mainService.getElementsOfBanners());
        model.addAttribute("posts", mainService.getPostsForCards());
        model.addAttribute("crowdFundings", mainService.getCrowdfundingsForCards());
        return "main/main";
    }
}
