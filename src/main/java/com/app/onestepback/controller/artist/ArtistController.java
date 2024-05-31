package com.app.onestepback.controller.artist;

import com.app.onestepback.domain.dto.artist.post.ArtistPostEditDTO;
import com.app.onestepback.domain.dto.artist.post.ArtistPostRegisterDTO;
import com.app.onestepback.domain.dto.artist.video.ArtistVideoListDTO;
import com.app.onestepback.domain.dto.artist.video.ArtistVideoRegisterDTO;
import com.app.onestepback.domain.vo.MemberVO;
import com.app.onestepback.domain.vo.Pagination;
import com.app.onestepback.service.artist.ArtistPostService;
import com.app.onestepback.service.artist.ArtistService;
import com.app.onestepback.service.artist.VideoPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@Slf4j
@RequestMapping("/artist")
@RequiredArgsConstructor
public class ArtistController {
    private final ArtistService artistService;
    private final ArtistPostService artistPostService;
    private final VideoPostService videoPostService;

    @GetMapping("/{artistId}")
    public String goToMainForm(@PathVariable("artistId") Long artistId,
                               HttpSession session,
                               Model model) {
        MemberVO member = (MemberVO) session.getAttribute("member");
        Long viewerId = member != null ? member.getId() : 0;
        Pagination pagination = new Pagination(1, 3, 3);
        HashMap<String, Object> content = new HashMap<>();

        content.put("artist", artistService.getArtistDetail(artistId, viewerId));
        System.out.println("pagination = " + pagination);
        content.put("artistPosts", artistPostService.getArtistPostsPage(artistId, viewerId, pagination));
        System.out.println("pagination = " + pagination);
        content.put("videoPosts", videoPostService.getArtistVideoPage(artistId, viewerId, pagination));

        model.addAttribute("content", content);
        return "artist/main";
    }

    @GetMapping("/{artistId}/sponsor")
    public String goToSponsorPage(@PathVariable("artistId") Long artistId, Model model) {
//        model.addAttribute("artist", artistService.getArtistDetail(artistId, ));

        return "artist/sponsor";
    }

    /////////////////////////////////////////////////// post /////////////////////////////////////////////////////////
    @GetMapping("/{artistId}/posts")
    public String goToPostList(@PathVariable("artistId") Long artistId,
                               @RequestParam(value = "page", required = false) Integer page,
                               HttpSession session,
                               Model model) {
        MemberVO member = (MemberVO) session.getAttribute("member");
        Long viewerId = member != null ? member.getId() : 0;
        Pagination pagination = new Pagination(page, 10, artistPostService.getPostCount(artistId));
        HashMap<String, Object> content = new HashMap<>();

        content.put("artist", artistService.getArtistDetail(artistId, viewerId));
        content.put("posts", artistPostService.getArtistPostsPage(artistId, viewerId, pagination));
        content.put("pagination", pagination);

        model.addAttribute("content", content);
        return "artist/post/list";
    }

    @GetMapping("/post/write")
    public String goToPostWriteForm(@ModelAttribute ArtistPostRegisterDTO artistPostRegisterDTO,
                                    HttpSession session) {
        MemberVO member = (MemberVO) session.getAttribute("member");

        if (!artistService.checkArtistExist(member.getId())) {
            throw new NoSuchElementException("해당 유저는 아티스트임을 조회 할 수 없음");
        }

        return "/artist/post/write";
    }

    @PostMapping("/post/write")
    public String saveArtistPost(ArtistPostRegisterDTO artistPostRegisterDTO,
                                 HttpSession session) {
        MemberVO member = (MemberVO) session.getAttribute("member");
        artistPostRegisterDTO.setArtistId(member.getId());

        try {
            artistPostService.savePost(artistPostRegisterDTO);
        } catch (Exception e) {
            log.error("아티스트 {}, 게시글 작성 오류 : {}", member.getId(), e.getMessage());
            return "/artist/post/write";
        }

        return "redirect:/artist/" + member.getId() + "/post/" + artistPostRegisterDTO.getPostId();
    }

    @GetMapping("/{artistId}/post/{postId}")
    public String goToPostDetailForm(@PathVariable Long artistId,
                                     @PathVariable Long postId,
                                     HttpSession session,
                                     Model model) {
        MemberVO member = (MemberVO) session.getAttribute("member");
        Long viewerId = member != null ? member.getId() : 0;


        model.addAttribute("post", artistPostService.getPostDetail(artistId, postId, viewerId));
        return "artist/post/detail";
    }

    @GetMapping("/post/edit")
    public String goToPostEditForm(@RequestParam("postId") Long postId,
                                   HttpSession session,
                                   Model model) {
        MemberVO member = (MemberVO) session.getAttribute("member");

        model.addAttribute("post", artistPostService.getEditPost(member.getId(), postId));
        return "artist/post/edit";
    }

    @PostMapping("/post/edit")
    public RedirectView postEditForm(ArtistPostEditDTO artistPostEditDTO,
                                     HttpSession session) {
        MemberVO member = (MemberVO) session.getAttribute("member");
        artistPostEditDTO.setArtistId(member.getId());

        try {
            artistPostService.editPost(artistPostEditDTO);
        } catch (IOException e) {
            log.error("아티스트 : {}, 수정오류 발생 : {}", member.getId(), e.getMessage());
        }

        return new RedirectView("/artist/" + artistPostEditDTO.getArtistId() + "/post/" + artistPostEditDTO.getPostId());
    }

    @PostMapping("/post/delete")
    public RedirectView erasePost(@RequestParam("postId") Long postId,
                                  HttpSession session) {
        MemberVO member = (MemberVO) session.getAttribute("member");

        artistPostService.erasePost(postId, member.getId());
        return new RedirectView("/artist/" + member.getId() + "/posts");
    }

    /////////////////////////////////////////////////// video /////////////////////////////////////////////////////////
    @GetMapping("/{artistId}/videos")
    public String goToVideoList(@PathVariable("artistId") Long artistId,
                                @RequestParam(value = "page", required = false) Integer page,
                                HttpSession session,
                                Model model) {
        MemberVO member = (MemberVO) session.getAttribute("member");
        Long viewerId = member != null ? member.getId() : 0;
        Pagination pagination = new Pagination(page, 10, videoPostService.getPostCount(artistId));
        HashMap<String, Object> content = new HashMap<>();

        List<ArtistVideoListDTO> posts = videoPostService.getArtistVideoPage(artistId, viewerId, pagination);

        System.out.println("posts = " + posts);

        content.put("artist", artistService.getArtistDetail(artistId, viewerId));
        content.put("posts", posts);
        content.put("pagination", pagination);

        model.addAttribute("content", content);
        return "artist/video/list";
    }

    @GetMapping("/video/write")
    public String goToVideoWriteForm(@ModelAttribute ArtistVideoRegisterDTO artistVideoRegisterDTO,
                                     HttpSession session) {
        MemberVO member = (MemberVO) session.getAttribute("member");

        if (!artistService.checkArtistExist(member.getId())) {
            throw new NoSuchElementException("해당 유저는 아티스트임을 조회 할 수 없음");
        }

        return "/artist/video/write";
    }

    @PostMapping("/video/write")
    public String saveArtistVideo(ArtistVideoRegisterDTO artistVideoRegisterDTO,
                                  HttpSession session) {
        MemberVO member = (MemberVO) session.getAttribute("member");
        artistVideoRegisterDTO.setArtistId(member.getId());

        try {
            videoPostService.savePost(artistVideoRegisterDTO);
        } catch (Exception e) {
            log.error("아티스트 {}, 영상게시글 작성 오류 : {}", member.getId(), e.getMessage());
            return "/artist/video/write";
        }

        return "redirect:/artist/" + member.getId() + "/video/" + artistVideoRegisterDTO.getPostId();
    }

//    @GetMapping("detail")
//    public void goToVideoDetail(@RequestParam("id") Long id, Model model){
//        VideoPostDTO nowVideoPost = videoPostService.getVideoPost(id);
//
//        Optional<VideoPostDTO> prevPost = videoPostService.getPrevPost(nowVideoPost);
//        Optional<VideoPostDTO> nextPost = videoPostService.getNextPost(nowVideoPost);
//
//        model.addAttribute("prevPost", prevPost.orElse(null));
//        model.addAttribute("nextPost", nextPost.orElse(null));
//        model.addAttribute("artist", videoPostService.getArtist(nowVideoPost.getMemberId()).get());
//        model.addAttribute("post", nowVideoPost);
//    }

//    @GetMapping("edit")
//    public void goToVideoEditForm(@RequestParam("id")Long id, VideoPostDTO videoPostDTO, Model model) {
//        model.addAttribute("post", videoPostService.getVideoPost(id));
//    }

//    @PostMapping("edit")
//    public RedirectView editForm(VideoPostDTO videoPostDTO, @RequestParam("numberOfTags")int numberOfTags){
//        videoPostService.editVideoPost(videoPostDTO, numberOfTags);
//
//        return new RedirectView("/artist/video/detail?id=" + videoPostDTO.getId());
//    }

//    @PostMapping("delete")
//    public RedirectView eraseVideo(@RequestParam("id") Long id, HttpSession session){
//        videoPostService.erasePost(id);
//
//        MemberVO member = (MemberVO) session.getAttribute("member");
//
//        return new RedirectView("/artist/video/list?memberId=" + member.getId());
//    }
}
