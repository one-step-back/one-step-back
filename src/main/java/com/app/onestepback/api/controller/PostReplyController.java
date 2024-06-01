package com.app.onestepback.api.controller;

import com.app.onestepback.api.response.DataResponse;
import com.app.onestepback.domain.dto.reply.PostReplyDTO;
import com.app.onestepback.domain.dto.reply.PostReplyWriteDTO;
import com.app.onestepback.domain.vo.MemberVO;
import com.app.onestepback.domain.vo.Pagination;
import com.app.onestepback.service.artist.PostReplyService;
import com.app.onestepback.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/reply")
public class PostReplyController {
    private final PostReplyService postReplyService;
    private final MemberService memberService;

    @GetMapping("/replies")
    public ResponseEntity<?> getReplies(@RequestParam("postId") Long postId,
                                        @RequestParam("page") int page) {
        // 리플라이는 rest로 작동해야 하므로 결과적으로 두개의 값을 보내야 됨
        // 1. 리플라이 내용들
        // 2. 리스트로 보냄과 동시에 이게 리플라이들의 마지막인지 아닌지를 구분할 수 있는 flag
        Map<String, Object> response = new HashMap<>();
        Pagination pagination = new Pagination(page, 20, postReplyService.getReplyCount(postId));

        // 리스트로 반납되는 현재의 리플라이들이 마지막인지 아닌지를 표시해주는 플래그
        boolean isEnd = false;
        // 만약 현재의 페이지가 마지막 페이지라면 true값 반납 & 애초부터 댓글이 없다면 강제적 트루값 반환
        if (page == pagination.getRealEnd()) {
            isEnd = true;
        } else if (pagination.getTotal() == 0) {
            isEnd = true;
        }

        // 해쉬맵을 이용하여 두개의 독립적인 값들을 반납
        try {
            response.put("replies", postReplyService.getAllReplies(postId, pagination));
            response.put("isEnd", isEnd);

            return new ResponseEntity<>(DataResponse.builder()
                    .success(true)
                    .data(response)
                    .build(), HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("댓글 리스트 출력 오류; postId : {}, 내용 : {}", postId, e.getMessage());
            return new ResponseEntity<>(DataResponse.builder()
                    .success(false)
                    .message("댓글을 불러오기를 실패했습니다.")
                    .build(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/write")
    public ResponseEntity<?> write(@RequestBody PostReplyWriteDTO postReplyWriteDTO,
                                   HttpSession session) {
        MemberVO member = (MemberVO) session.getAttribute("member");

        try {
            postReplyWriteDTO.setMemberId(member.getId());

            postReplyService.saveReply(postReplyWriteDTO);

            PostReplyDTO response = PostReplyDTO.builder()
                    .replyId(postReplyWriteDTO.getPostId())
                    .memberId(member.getId())
                    .content(postReplyWriteDTO.getContent())
                    .createdTime(LocalDateTime.now())
                    .nickname(member.getMemberNickname())
                    .kakaoProfileUrl(member.getMemberKakaoProfileUrl())
                    .profileName(member.getMemberProfileName())
                    .profilePath(member.getMemberProfilePath())
                    .build();

            return new ResponseEntity<>(DataResponse.builder()
                    .success(true)
                    .message("댓글이 등록되었습니다.")
                    .data(response)
                    .build(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("댓글 등록 실패; 사용자 : {}, 포스트 : {}, 내용 : {}", member.getId(), postReplyWriteDTO.getPostId(), e.getMessage());

            return new ResponseEntity<>(DataResponse.builder()
                    .success(false)
                    .message("댓글을 등록하지 못했습니다.")
                    .build(), HttpStatus.BAD_REQUEST);
        }
    }
}
