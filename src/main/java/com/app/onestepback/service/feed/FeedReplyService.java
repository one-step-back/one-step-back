package com.app.onestepback.service.feed;

import com.app.onestepback.domain.dto.feed.reply.FeedReplyDTO;
import com.app.onestepback.service.feed.cmd.FeedReplyWriteCmd;

import java.util.List;

/**
 * 피드 댓글에 대한 답글(Reply/대댓글)과 관련된 비즈니스 로직을 정의하는 서비스 인터페이스입니다.
 */
public interface FeedReplyService {

    /**
     * 특정 댓글에 새로운 답글을 작성합니다.
     *
     * @param cmd 답글 작성에 필요한 데이터를 담은 커맨드 객체
     * @return 생성된 답글의 고유 식별자
     */
    Long write(FeedReplyWriteCmd cmd);

    /**
     * 특정 댓글에 종속된 모든 답글 목록을 조회합니다.
     *
     * @param commentId 조회할 대상 댓글의 고유 식별자
     * @return 답글 목록 DTO 리스트
     */
    List<FeedReplyDTO> getList(Long commentId);

    /**
     * 작성된 답글의 내용을 수정합니다.
     *
     * @param replyId  수정할 답글의 고유 식별자
     * @param memberId 수정을 요청한 회원의 고유 식별자 (권한 검증용)
     * @param content  수정할 답글 내용
     */
    void modify(Long replyId, Long memberId, String content);

    /**
     * 작성된 답글을 논리적으로 삭제(Soft Delete) 처리합니다.
     *
     * @param replyId  삭제할 답글의 고유 식별자
     * @param memberId 삭제를 요청한 회원의 고유 식별자 (권한 검증용)
     */
    void delete(Long replyId, Long memberId);
}