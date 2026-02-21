package com.app.onestepback.web.api.v1.feed;

import com.app.onestepback.domain.dto.feed.comment.FeedCommentDTO;
import com.app.onestepback.domain.dto.feed.comment.FeedCommentSearchCond;
import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.global.annotation.LoginUser;
import com.app.onestepback.global.common.Slice;
import com.app.onestepback.service.feed.FeedCommentService;
import com.app.onestepback.web.api.v1.feed.request.FeedCommentUpdateRequest;
import com.app.onestepback.web.api.v1.feed.request.FeedCommentWriteRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class FeedCommentApiV1 {

    private final FeedCommentService feedCommentService;

    /**
     * 특정 피드에 새로운 댓글을 등록합니다.
     *
     * @param user 현재 인가된 사용자의 세션 객체
     * @param body 작성할 댓글 내용과 대상 피드 정보가 담긴 요청 객체
     * @return 생성된 댓글의 고유 식별자
     */
    @PostMapping
    public ResponseEntity<Long> saveComment(
            @LoginUser SessionUser user,
            @RequestBody @Valid FeedCommentWriteRequest body
    ) {
        Long savedId = feedCommentService.write(body.toCmd(user.getId()));
        return ResponseEntity.ok(savedId);
    }

    /**
     * 특정 피드에 종속된 댓글 목록을 무한 스크롤(Slice) 방식으로 조회합니다.
     *
     * @param feedId        조회할 대상 피드의 고유 식별자
     * @param lastCommentId 이전 페이지의 마지막 댓글 식별자 (커서 기반 페이징)
     * @param size          조회할 데이터의 최대 개수 (기본값: 10)
     * @return 댓글 목록 데이터가 포함된 슬라이스(Slice) 응답 객체
     */
    @GetMapping("/list")
    public ResponseEntity<Slice<FeedCommentDTO>> getCommentSlice(
            @RequestParam("feedId") Long feedId,
            @RequestParam(value = "lastCommentId", required = false) Long lastCommentId,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        FeedCommentSearchCond cond = FeedCommentSearchCond.builder()
                .feedId(feedId)
                .lastCommentId(lastCommentId)
                .size(size)
                .build();

        Slice<FeedCommentDTO> result = feedCommentService.getSlice(cond);
        return ResponseEntity.ok(result);
    }

    /**
     * 본인이 작성한 특정 댓글의 내용을 수정합니다.
     *
     * @param commentId 수정할 댓글의 고유 식별자
     * @param user      현재 인가된 사용자의 세션 객체 (소유권 검증용)
     * @param body      수정할 댓글 내용이 담긴 요청 객체
     * @return 상태 코드 204 (No Content)
     */
    @PatchMapping("/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable("commentId") Long commentId,
            @LoginUser SessionUser user,
            @RequestBody @Valid FeedCommentUpdateRequest body
    ) {
        feedCommentService.modify(commentId, user.getId(), body.content());
        return ResponseEntity.noContent().build();
    }

    /**
     * 본인이 작성한 특정 댓글을 논리적으로 삭제(Soft Delete) 처리합니다.
     *
     * @param commentId 삭제할 댓글의 고유 식별자
     * @param user      현재 인가된 사용자의 세션 객체 (소유권 검증용)
     * @return 상태 코드 204 (No Content)
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> softDeleteComment(
            @PathVariable("commentId") Long commentId,
            @LoginUser SessionUser user
    ) {
        feedCommentService.softDelete(commentId, user.getId());
        return ResponseEntity.noContent().build();
    }
}