package com.app.onestepback.controller;

import com.app.onestepback.domain.ArtistPostDTO;
import com.app.onestepback.domain.MemberVO;
import com.app.onestepback.domain.Pagination;
import com.app.onestepback.domain.SubscriptionVO;
import com.app.onestepback.service.ArtistPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/artist/post/*")
public class ArtistPostController {
    private final ArtistPostService artistPostService;

    @GetMapping("list")
    public void goToPostList(Pagination pagination, @RequestParam("memberId") Long memberId, @RequestParam(value = "page", required = false) Integer page, Model model) {
        model.addAttribute("artist", artistPostService.getArtist(memberId).get());

        pagination.setTotal(artistPostService.getPostCount(memberId));
        pagination.setPage(page);
        pagination.setRowCount(10);
        pagination.progress();
        model.addAttribute("pagination", pagination);
        model.addAttribute("posts", artistPostService.getAllPosts(memberId, pagination));
    }

    @GetMapping("write")
    public void goToPostWriteForm(ArtistPostDTO artistPostDTO, HttpSession session, Model model) {
        MemberVO memberSession = (MemberVO) session.getAttribute("member");

        log.info(String.valueOf(memberSession.getId()));
        model.addAttribute("memberId", memberSession.getId());
    }

    @PostMapping("write")
    public RedirectView saveArtistPost(ArtistPostDTO artistPostDTO, @RequestParam("numberOfTags") int numberOfTags, @RequestParam(value = "uuid", required = false) List<String> uuids, @RequestParam(value = "uploadFile", required = false) List<MultipartFile> uploadFiles) {
        artistPostService.savePost(artistPostDTO, numberOfTags, uuids, uploadFiles);

        return new RedirectView("/artist/post/detail?id=" + artistPostDTO.getId());
    }

    @GetMapping("detail")
    public void goToPostDetailForm(@RequestParam("id") Long id, Model model) {

        ArtistPostDTO nowPost = artistPostService.getPost(id);

        Optional<ArtistPostDTO> prevPost = artistPostService.getPrevPost(nowPost);
        Optional<ArtistPostDTO> nextPost = artistPostService.getNextPost(nowPost);

        model.addAttribute("prevPost", prevPost.orElse(null));
        model.addAttribute("nextPost", nextPost.orElse(null));
        model.addAttribute("artist", artistPostService.getArtist(nowPost.getMemberId()).get());
        model.addAttribute("post", nowPost);
    }

    @GetMapping("edit")
    public void goToEditForm(@RequestParam("id")Long id, ArtistPostDTO artistPostDTO, Model model) {
        model.addAttribute("post", artistPostService.getPost(id));
        model.addAttribute("files", artistPostService.getAllFiles(id));
    }

    @PostMapping("edit")
    public RedirectView editForm(ArtistPostDTO artistPostDTO, @RequestParam("numberOfTags") int numberOfTags, @RequestParam(value = "uuid", required = false) List<String> uuids, @RequestParam(value = "uploadFile", required = false) List<MultipartFile> uploadFiles){
        if (uploadFiles != null) {
            log.info(String.valueOf(uploadFiles.size()));
        } else {
            log.info("No files uploaded.");
        }

        artistPostService.editPost(artistPostDTO, numberOfTags, uuids, uploadFiles);

        return new RedirectView("/artist/post/detail?id=" + artistPostDTO.getId());
    }
}
