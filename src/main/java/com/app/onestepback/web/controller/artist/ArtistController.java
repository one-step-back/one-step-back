package com.app.onestepback.web.controller.artist;

import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.global.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/artist")
@RequiredArgsConstructor
public class ArtistController {

    /**
     * 아티스트 등록(가입) 페이지로 이동합니다.
     */
    @GetMapping("/register")
    public String register(@LoginUser SessionUser __) {
        return "artist/register";
    }
}