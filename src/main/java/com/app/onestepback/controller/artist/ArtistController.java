package com.app.onestepback.controller.artist;

import com.app.onestepback.domain.dto.ArtistPostDTO;
import com.app.onestepback.domain.vo.MemberVO;
import com.app.onestepback.domain.vo.Pagination;
import com.app.onestepback.exception.CustomException;
import com.app.onestepback.service.artist.ArtistPostService;
import com.app.onestepback.service.artist.ArtistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("/artist")
@RequiredArgsConstructor
public class ArtistController {
    private final ArtistService artistService;
    private final ArtistPostService artistPostService;

    @GetMapping("/{artistId}")
    public String goToMainForm(@PathVariable("artistId") Long artistId, Model model) {
        if (artistId == null) {
            throw new CustomException("존재하지 않는 아티스트");
        }
        model.addAttribute("artist", artistService.getArtist(artistId).get());
        model.addAttribute("posts", artistService.get3Posts(artistId));
        model.addAttribute("videos", artistService.get3Videos(artistId));
        return "artist/main";
    }

    @GetMapping("/{artistId}/sponsor")
    public String goToSponsorPage(@PathVariable("artistId") Long artistId, Model model) {
        model.addAttribute("artist", artistService.getArtist(artistId).get());

        return "artist/sponsor";
    }

    /////////////////////////////////////////////////// post /////////////////////////////////////////////////////////
    @GetMapping("/{artistId}/post/list")
    public String goToPostList(@PathVariable("artistId") Long artistId,
                               @RequestParam(value = "page", required = false) Integer page,
                               Model model) {
        model.addAttribute("artist", artistPostService.getArtist(artistId).get());

//        pagination.setTotal(artistPostService.getPostCount(artistId));
//        pagination.setPage(page);
//        pagination.setRowCount(10);
//        pagination.progress();
        // 생성자로 간결화 처리.
        Pagination pagination = new Pagination(page, 10, artistPostService.getPostCount(artistId));

        model.addAttribute("pagination", pagination);
        model.addAttribute("posts", artistPostService.getAllPosts(artistId, pagination));
        return "artist/post/list";
    }

    @GetMapping("/{artistId}/post/write")
    public String goToPostWriteForm(ArtistPostDTO artistPostDTO,
                                    HttpSession session,
                                    Model model) {
        MemberVO memberSession = (MemberVO) session.getAttribute("member");

        log.info(String.valueOf(memberSession.getId()));
        model.addAttribute("memberId", memberSession.getId());
        return "artist/post/write";
    }

    @PostMapping("write")
    public RedirectView saveArtistPost(ArtistPostDTO artistPostDTO,
                                       @RequestParam("numberOfTags") int numberOfTags,
                                       @RequestParam(value = "uuid", required = false) List<String> uuids,
                                       @RequestParam(value = "uploadFile", required = false) List<MultipartFile> uploadFiles) {
        artistPostService.savePost(artistPostDTO, numberOfTags, uuids, uploadFiles);

        return new RedirectView("/artist/post/detail?id=" + artistPostDTO.getId());
    }

    @GetMapping("/{artistId}/post/detail")
    public String goToPostDetailForm(@RequestParam("id") Long id, Model model) {

        // todo: 총체적으로 관리할 수있는 dto로 만들고 서비스 단에서 합칠 수 있도록 구조 재편 필요, 가능하다면 다중 쿼리문은 회피할 것.
        ArtistPostDTO nowPost = artistPostService.getPost(id);

        Optional<ArtistPostDTO> prevPost = artistPostService.getPrevPost(nowPost);
        Optional<ArtistPostDTO> nextPost = artistPostService.getNextPost(nowPost);

        model.addAttribute("prevPost", prevPost.orElse(null));
        model.addAttribute("nextPost", nextPost.orElse(null));
        model.addAttribute("artist", artistPostService.getArtist(nowPost.getMemberId()).get());
        model.addAttribute("post", nowPost);
        model.addAttribute("images", artistPostService.getAllFiles(id));
        return "artist/post/detail";
    }

    @GetMapping("/{artistId}/post/edit")
    public String goToEditForm(@RequestParam("id") Long id, ArtistPostDTO artistPostDTO, Model model) {
        model.addAttribute("post", artistPostService.getPost(id));
        model.addAttribute("files", artistPostService.getAllFiles(id));
        return "artist/post/edit";
    }

    @PostMapping("edit")
    public RedirectView editForm(ArtistPostDTO artistPostDTO,
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

}
