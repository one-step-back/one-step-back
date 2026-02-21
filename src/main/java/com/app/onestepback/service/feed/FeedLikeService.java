package com.app.onestepback.service.feed;

/**
 * 피드(게시글)의 좋아요(Like) 상태를 관리하는 비즈니스 로직 인터페이스입니다.
 */
public interface FeedLikeService {

    /**
     * 특정 피드에 대한 사용자의 좋아요 상태를 활성화 또는 비활성화합니다.
     *
     * @param feedId   좋아요 상태를 변경할 대상 피드의 고유 식별자
     * @param memberId 상태 변경을 요청한 회원의 고유 식별자
     * @param isLike   true일 경우 좋아요 데이터 추가, false일 경우 삭제
     */
    void setLike(Long feedId, Long memberId, boolean isLike);
}