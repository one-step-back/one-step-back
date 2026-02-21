package com.app.onestepback.web.api.v1.feed;

import com.app.onestepback.domain.dto.feed.reply.FeedReplyDTO;
import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.global.annotation.LoginUser;
import com.app.onestepback.service.feed.FeedReplyService;
import com.app.onestepback.web.api.v1.feed.request.FeedReplyUpdateRequest;
import com.app.onestepback.web.api.v1.feed.request.FeedReplyWriteRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/replies")
@RequiredArgsConstructor
public class FeedReplyApiV1 {

    private final FeedReplyService feedReplyService;

    /**
     * 특정 댓글에 종속된 새로운 답글(대댓글)을 등록합니다.
     *
     * @param user 현재 인가된 사용자의 세션 객체
     * @param body 작성할 답글 내용과 대상 댓글 정보가 담긴 요청 객체
     * @return 생성된 답글의 고유 식별자
     */
    @PostMapping
    public ResponseEntity<Long> saveReply(
            @LoginUser SessionUser user,
            @RequestBody @Valid FeedReplyWriteRequest body
    ) {
        Long savedId = feedReplyService.write(body.toCmd(user.getId()));
        return ResponseEntity.ok(savedId);
    }

    /**
     * 특정 댓글에 등록된 모든 답글 목록을 조회합니다.
     *
     * @param commentId 조회할 대상 댓글의 고유 식별자
     * @return 해당 댓글에 종속된 답글 리스트 데이터
     */
    @GetMapping
    public ResponseEntity<List<FeedReplyDTO>> getReplyList(
            @RequestParam("commentId") Long commentId
    ) {
        List<FeedReplyDTO> replies = feedReplyService.getList(commentId);
        return ResponseEntity.ok(replies);
    }

    /**
     * 본인이 작성한 답글(대댓글)의 내용을 수정합니다.
     *
     * @param replyId 수정할 답글의 고유 식별자
     * @param user    현재 인가된 사용자의 세션 객체 (소유권 검증용)
     * @param body    수정할 내용이 담긴 요청 객체
     * @return 상태 코드 204 (No Content)
     */
    @PatchMapping("/{replyId}")
    public ResponseEntity<?> updateReply(
            @PathVariable("replyId") Long replyId,
            @LoginUser SessionUser user,
            @RequestBody @Valid FeedReplyUpdateRequest body
    ) {
        feedReplyService.modify(replyId, user.getId(), body.content());
        return ResponseEntity.noContent().build();
    }

    /**
     * 본인이 작성한 답글(대댓글)을 논리적으로 삭제(Soft Delete) 처리합니다.
     *
     * @param replyId 삭제할 답글의 고유 식별자
     * @param user    현재 인가된 사용자의 세션 객체 (소유권 검증용)
     * @return 상태 코드 204 (No Content)
     */
    @DeleteMapping("/{replyId}")
    public ResponseEntity<?> deleteReply(
            @PathVariable("replyId") Long replyId,
            @LoginUser SessionUser user
    ) {
        feedReplyService.delete(replyId, user.getId());
        return ResponseEntity.noContent().build();
    }
}