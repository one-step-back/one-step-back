package com.app.onestepback.service.follow;

import com.app.onestepback.domain.dto.follow.FollowDTO;
import com.app.onestepback.domain.dto.follow.FollowSearchCond;

import java.util.List;

/**
 * 아티스트 구독(Follow)과 관련된 비즈니스 로직을 정의하는 서비스 인터페이스입니다.
 * <p>
 * 구독 상태 변경 및 구독자/구독 중인 아티스트 목록 조회 기능을 제공합니다.
 * </p>
 */
public interface FollowService {

    /**
     * 특정 아티스트에 대한 사용자의 구독 상태를 활성화 또는 비활성화합니다.
     *
     * @param artistId 구독 대상 아티스트의 고유 식별자
     * @param memberId 구독을 요청한 회원의 고유 식별자
     * @param status   true일 경우 구독 활성화(INSERT), false일 경우 구독 취소(DELETE)
     */
    void setSubscription(Long artistId, Long memberId, boolean status);

    /**
     * 특정 아티스트를 구독하고 있는 모든 회원의 식별자 목록을 조회합니다.
     * <p>
     * 단체 알림 발송(Broadcast) 등의 목적에 주로 활용됩니다.
     * </p>
     *
     * @param artistId 대상을 식별할 아티스트의 고유 식별자
     * @return 구독자(회원) 식별자 리스트
     */
    List<Long> findSubscriberIds(long artistId);

    /**
     * 특정 회원이 구독 중인 아티스트 목록을 무한 스크롤(Slice) 방식으로 조회하기 위한 데이터를 반환합니다.
     *
     * @param cond 조회할 회원의 식별자 및 페이징 정보가 담긴 검색 조건 객체
     * @return 구독 중인 아티스트(Following) 정보 DTO 리스트
     */
    List<FollowDTO.Following> getFollowings(FollowSearchCond cond);

    /**
     * 특정 아티스트를 구독 중인 팬(Follower) 목록을 조회합니다.
     *
     * @param artistId 조회를 요청받은 아티스트의 고유 식별자
     * @return 아티스트의 구독자(Follower) 정보 DTO 리스트
     */
    List<FollowDTO.Follower> getArtistFollowers(Long artistId);
}