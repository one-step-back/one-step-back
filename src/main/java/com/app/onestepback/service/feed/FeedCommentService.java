package com.app.onestepback.service.feed;

import com.app.onestepback.domain.dto.feed.comment.FeedCommentDTO;
import com.app.onestepback.domain.dto.feed.comment.FeedCommentSearchCond;
import com.app.onestepback.global.common.Slice;
import com.app.onestepback.service.feed.cmd.FeedCommentWriteCmd;

/**
 * 피드 댓글(Comment)과 관련된 비즈니스 로직을 정의하는 서비스 인터페이스입니다.
 */
public interface FeedCommentService {

    /**
     * 특정 피드에 새로운 댓글을 작성합니다.
     *
     * @param cmd 댓글 작성에 필요한 데이터를 담은 커맨드 객체
     * @return 생성된 댓글의 고유 식별자
     */
    Long write(FeedCommentWriteCmd cmd);

    /**
     * 특정 피드의 댓글 목록을 무한 스크롤(Slice) 방식으로 조회합니다.
     *
     * @param cond 조회할 댓글의 검색 조건(피드 ID, 페이징 정보 등)이 담긴 객체
     * @return 댓글 목록 데이터가 포함된 슬라이스(Slice) 객체
     */
    Slice<FeedCommentDTO> getSlice(FeedCommentSearchCond cond);

    /**
     * 작성된 댓글의 내용을 수정합니다.
     *
     * @param commentId 수정할 댓글의 고유 식별자
     * @param memberId  수정을 요청한 회원의 고유 식별자 (권한 검증용)
     * @param content   수정할 댓글 내용
     */
    void modify(Long commentId, Long memberId, String content);

    /**
     * 작성된 댓글을 논리적으로 삭제(Soft Delete) 처리합니다.
     *
     * @param commentId 삭제할 댓글의 고유 식별자
     * @param memberId  삭제를 요청한 회원의 고유 식별자 (권한 검증용)
     */
    void softDelete(Long commentId, Long memberId);
}