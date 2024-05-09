package com.app.onestepback.controller.artist;

import com.app.onestepback.domain.dto.artist.*;
import com.app.onestepback.domain.vo.MemberVO;
import com.app.onestepback.domain.vo.Pagination;
import com.app.onestepback.exception.CustomException;
import com.app.onestepback.service.artist.ArtistPostService;
import com.app.onestepback.service.artist.ArtistService;
import com.app.onestepback.service.artist.VideoPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/artist")
@RequiredArgsConstructor
public class ArtistController {
    private final ArtistService artistService;
    private final ArtistPostService artistPostService;
    private final VideoPostService videoPostService;

    @GetMapping("/{artistId}")
    public String goToMainForm(@PathVariable("artistId") Long artistId, Model model) {
        ArtistDTO artist = artistService.getArtist(artistId);

        model.addAttribute("artist", artist);
//        model.addAttribute("posts", artistService.get3Posts(artistId));
//        model.addAttribute("videos", artistService.get3Videos(artistId));
        return "artist/main";
    }

    @GetMapping("/{artistId}/sponsor")
    public String goToSponsorPage(@PathVariable("artistId") Long artistId, Model model) {
        model.addAttribute("artist", artistService.getArtist(artistId));

        return "artist/sponsor";
    }

    /////////////////////////////////////////////////// post /////////////////////////////////////////////////////////
    @GetMapping("/{artistId}/posts")
    public String goToPostList(@PathVariable("artistId") Long artistId,
                               @RequestParam(value = "page", required = false) Integer page,
                               HttpSession session,
                               Model model) {
        MemberVO memberSession = (MemberVO) session.getAttribute("member");
        Long viewerId = memberSession != null ? memberSession.getId() : 0;
        System.out.println("viewerId = " + viewerId);

        model.addAttribute("artist", artistPostService.getArtist(artistId).get());

//        pagination.setTotal(artistPostService.getPostCount(artistId));
//        pagination.setPage(page);
//        pagination.setRowCount(10);
//        pagination.progress();
        // 생성자로 간결화 처리.
        Pagination pagination = new Pagination(page, 10, artistPostService.getPostCount(artistId));
        List<ArtistPostListDTO> contents = artistPostService.getArtistPostsPage(artistId, viewerId, pagination);

        System.out.println("contents = " + contents);

        model.addAttribute("pagination", pagination);
        model.addAttribute("posts", contents);
        return "artist/post/list";
    }

    @GetMapping("/post/write")
    public String goToPostWriteForm(@ModelAttribute ArtistPostRegisterDTO artistPostRegisterDTO,
                                    HttpSession session,
                                    Model model) {
        MemberVO memberSession = (MemberVO) session.getAttribute("member");

        model.addAttribute("memberId", memberSession.getId());
        return "/artist/post/write";
    }

    @PostMapping("/post/write")
    public String saveArtistPost(ArtistPostRegisterDTO artistPostRegisterDTO,
                                 HttpSession session) {

        MemberVO memberSession = (MemberVO) session.getAttribute("member");
        artistPostRegisterDTO.setMemberId(memberSession.getId());

        try {
            artistPostService.savePost(artistPostRegisterDTO);
        } catch (Exception e) {
            log.error("아티스트 {}, 게시글 작성 오류 : {}", memberSession.getId(), e.getMessage());
            return "/artist/" + memberSession.getId() + "/post/write";
        }

        return "redirect:/artist/" + memberSession.getId() + "/post/" + artistPostRegisterDTO.getPostId();
    }

    @GetMapping("/{artistId}/post/{postId}")
    public String goToPostDetailForm(@PathVariable Long artistId,
                                     @PathVariable Long postId,
                                     Model model) {
        ArtistPostDetailDTO content = artistPostService.getPostDetail(artistId, postId);

        System.out.println("content = " + content);

        model.addAttribute("post", content);
        return "artist/post/detail";
    }

    @GetMapping("/{artistId}/post/edit")
    public String goToPostEditForm(@RequestParam("id") Long id, ArtistPostDTO artistPostDTO, Model model) {
//        model.addAttribute("post", artistPostService.getPost(id));
//        model.addAttribute("files", artistPostService.getAllFiles(id));
        return "artist/post/edit";
    }

    @PostMapping("edit")
    public RedirectView postEditForm(ArtistPostDTO artistPostDTO,
                                     @RequestParam("numberOfTags") int numberOfTags,
                                     @RequestParam(value = "uuid", required = false) List<String> uuids,
                                     @RequestParam(value = "uploadFile", required = false) List<MultipartFile> uploadFiles) {
        artistPostService.editPost(artistPostDTO, numberOfTags, uuids, uploadFiles);

        return new RedirectView("/artist/post/detail?id=" + artistPostDTO.getId());
    }

    @PostMapping("delete")
    public RedirectView erasePost(@RequestParam("id") Long id, HttpSession session) {
        artistPostService.erasePost(id);

        MemberVO member = (MemberVO) session.getAttribute("member");

        return new RedirectView("/artist/post/list?memberId=" + member.getId());
    }

    /////////////////////////////////////////////////// video /////////////////////////////////////////////////////////
    @GetMapping("/{artistId}/video/list")
    public void goToVideoList(@PathVariable("artistId") Long artistId,
                              @RequestParam(value = "page", required = false) Integer page,
                              Model model) {
        model.addAttribute("artist", videoPostService.getArtist(artistId).get());

        Pagination pagination = new Pagination(page, 10, videoPostService.getPostCount(artistId));
        model.addAttribute("pagination", pagination);
        model.addAttribute("videos", videoPostService.getAllVideos(artistId, pagination));
    }

//    @GetMapping("write")
//    public void goToVideoWriteForm(VideoPostDTO videoPostDTO, HttpSession session, Model model) {
//        MemberVO memberSession = (MemberVO) session.getAttribute("member");
//
//        model.addAttribute("memberId", memberSession.getId());
//    }
//
//    @PostMapping("write")
//    public RedirectView saveArtistVideo(VideoPostDTO videoPostDTO, @RequestParam("numberOfTags") int numberOfTags, @RequestParam(value = "uuid", required = false) List<String> uuids, @RequestParam(value = "uploadFile", required = false) List<MultipartFile> uploadFiles) {
//        videoPostService.savePost(videoPostDTO, numberOfTags);
//
//        return new RedirectView("/artist/video/detail?id=" + videoPostDTO.getId());
//    }

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
