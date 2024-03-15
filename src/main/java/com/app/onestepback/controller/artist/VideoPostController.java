//package com.app.onestepback.controller.artist;
//
//import com.app.onestepback.domain.vo.MemberVO;
//import com.app.onestepback.domain.vo.Pagination;
//import com.app.onestepback.domain.dto.VideoPostDTO;
//import com.app.onestepback.service.artist.VideoPostService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.servlet.view.RedirectView;
//
//import javax.servlet.http.HttpSession;
//import java.util.List;
//import java.util.Optional;
//
//@Controller
//@RequiredArgsConstructor
//@Slf4j
//@RequestMapping("/artist/video/*")
//public class VideoPostController {
//    private final VideoPostService videoPostService;
//
//    @GetMapping("list")
//    public void goToVideoList(Pagination pagination, @RequestParam("memberId") Long memberId, @RequestParam(value = "page", required = false) Integer page, Model model){
//        model.addAttribute("artist", videoPostService.getArtist(memberId).get());
//
//        pagination.setTotal(videoPostService.getPostCount(memberId));
//        pagination.setPage(page);
//        pagination.setRowCount(10);
//        pagination.progress();
//        model.addAttribute("pagination", pagination);
//        model.addAttribute("videos", videoPostService.getAllVideos(memberId, pagination));
//    }
//
//    @GetMapping("write")
//    public void goToPostWriteForm(VideoPostDTO videoPostDTO, HttpSession session, Model model) {
//        MemberVO memberSession = (MemberVO) session.getAttribute("member");
//
//        model.addAttribute("memberId", memberSession.getId());
//    }
//
//    @PostMapping("write")
//    public RedirectView saveArtistPost(VideoPostDTO videoPostDTO, @RequestParam("numberOfTags") int numberOfTags, @RequestParam(value = "uuid", required = false) List<String> uuids, @RequestParam(value = "uploadFile", required = false) List<MultipartFile> uploadFiles) {
//        videoPostService.savePost(videoPostDTO, numberOfTags);
//
//        return new RedirectView("/artist/video/detail?id=" + videoPostDTO.getId());
//    }
//
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
//
//    @GetMapping("edit")
//    public void goToEditForm(@RequestParam("id")Long id, VideoPostDTO videoPostDTO, Model model) {
//        model.addAttribute("post", videoPostService.getVideoPost(id));
//    }
//
//    @PostMapping("edit")
//    public RedirectView editForm(VideoPostDTO videoPostDTO, @RequestParam("numberOfTags")int numberOfTags){
//        videoPostService.editVideoPost(videoPostDTO, numberOfTags);
//
//        return new RedirectView("/artist/video/detail?id=" + videoPostDTO.getId());
//    }
//
//    @PostMapping("delete")
//    public RedirectView erasePost(@RequestParam("id") Long id, HttpSession session){
//        videoPostService.erasePost(id);
//
//        MemberVO member = (MemberVO) session.getAttribute("member");
//
//        return new RedirectView("/artist/video/list?memberId=" + member.getId());
//    }
//}
