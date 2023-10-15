package com.app.onestepback.controller;

import com.app.onestepback.domain.ArtistDTO;
import com.app.onestepback.domain.ArtistPostDTO;
import com.app.onestepback.domain.VideoDTO;
import com.app.onestepback.repository.ArtistPostDAO;
import com.app.onestepback.service.ArtistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("/artist/*")
@RequiredArgsConstructor
public class ArtistController {
    private final ArtistService artistService;

    @GetMapping("main")
    public void goToMainForm(@RequestParam("id") Long id, Model model) {
        model.addAttribute("artist", artistService.getArtist(id).get());
    }

    @GetMapping("get-counts")
    @ResponseBody
    public int[] getCountOfSubscriber(@RequestParam("id") Long id) {
        int[] counts = new int[2];
        // 구독자 수
        counts[0] = artistService.getCountOfSubscriber(id);
        // 아티스트 포스트 + 비디오 포스트
        counts[1] = artistService.getCountOfPost(id) + artistService.getCountOfVideo(id);

        return counts;
    }

    @GetMapping("get-3posts")
    @ResponseBody
    public List<ArtistPostDTO> load3Posts(@RequestParam("id") Long id) {
        List<ArtistPostDTO> posts = artistService.get3Posts(id);

        for (ArtistPostDTO post : posts) {
            List<String> tags = artistService.getAllTagsOfPosts(post.getId());

            for (int i = 0; i < tags.size(); i++) {
                switch (i) {
                    case 0:
                        post.setTag1(tags.get(i));
                        break;
                    case 1:
                        post.setTag2(tags.get(i));
                        break;
                    case 2:
                        post.setTag3(tags.get(i));
                        break;
                    case 3:
                        post.setTag4(tags.get(i));
                        break;
                    case 4:
                        post.setTag5(tags.get(i));
                        break;
                }
            }
        }

        return posts;
    }

    @GetMapping("get-3videos")
    @ResponseBody
    public List<VideoDTO> load3Videos(@RequestParam("id") Long id) {
        List<VideoDTO> videos = artistService.get3Videos(id);

        for (VideoDTO video : videos) {
            List<String> tags = artistService.getAllTagsOfVideos(video.getId());

            for (int i = 0; i < tags.size(); i++) {
                log.info(tags.get(i));
                switch (i) {
                    case 0:
                        video.setTag1(tags.get(i));
                        break;
                    case 1:
                        video.setTag2(tags.get(i));
                        break;
                    case 2:
                        video.setTag3(tags.get(i));
                        break;
                    case 3:
                        video.setTag4(tags.get(i));
                        break;
                    case 4:
                        video.setTag5(tags.get(i));
                        break;
                }
            }
        }

        return videos;
    }

    @GetMapping("sponsor")
    public void goToSponsorForm() {
        ;
    }

    @GetMapping("/post/write")
    public void goToPostWriteForm() {
        ;
    }

    @GetMapping("/post/edit")
    public void goToPostEditForm() {
        ;
    }

    @GetMapping("/post/detail")
    public void goToPostDetailForm() {
        ;
    }
}
