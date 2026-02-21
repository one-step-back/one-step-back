package com.app.onestepback.web.api.v1.follow;

import com.app.onestepback.domain.dto.follow.FollowDTO;
import com.app.onestepback.domain.dto.follow.FollowSearchCond;
import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.global.annotation.LoginUser;
import com.app.onestepback.global.common.response.ApiResponse;
import com.app.onestepback.global.exception.BusinessException;
import com.app.onestepback.global.exception.ErrorCode;
import com.app.onestepback.service.follow.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/follows")
@RequiredArgsConstructor
public class FollowApiV1 {

    private final FollowService followService;

    /**
     * 특정 아티스트에 대한 팔로우(구독)를 요청합니다.
     * <p>
     * 사용자가 자기 자신을 팔로우하려는 시도는 시스템 정책상 차단됩니다.
     * </p>
     *
     * @param artistId 팔로우할 대상 아티스트의 고유 식별자
     * @param user     현재 인가된 사용자의 세션 객체
     * @return 처리 결과 메시지가 포함된 표준 응답 객체
     */
    @PostMapping("/{artistId}")
    public ResponseEntity<?> followArtist(
            @PathVariable("artistId") Long artistId,
            @LoginUser SessionUser user
    ) {
        if (user.getId().equals(artistId)) {
            throw new BusinessException(ErrorCode.SELF_FOLLOW_NOT_ALLOWED);
        }

        followService.setSubscription(artistId, user.getId(), true);

        return ResponseEntity.ok(ApiResponse.ok("팔로우 처리되었습니다."));
    }

    /**
     * 현재 로그인한 사용자가 팔로우 중인 아티스트 목록을 조회합니다.
     *
     * @param user 현재 인가된 사용자의 세션 객체
     * @param sort 목록 정렬 조건 (기본값: LATEST)
     * @return 팔로우 중인 아티스트 목록 데이터가 포함된 표준 응답 객체
     */
    @GetMapping("/list")
    public ResponseEntity<?> getMyFollowings(
            @LoginUser SessionUser user,
            @RequestParam(value = "sort", required = false, defaultValue = "LATEST") FollowSearchCond.Sort sort
    ) {
        FollowSearchCond cond = new FollowSearchCond(user.getId(), sort);
        List<FollowDTO.Following> list = followService.getFollowings(cond);

        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    /**
     * 인가된 아티스트 권한으로 자신을 팔로우하고 있는 팬(구독자) 목록을 조회합니다.
     *
     * @param user 현재 인가된 아티스트의 세션 객체
     * @return 아티스트를 팔로우하는 구독자 목록 데이터가 포함된 표준 응답 객체
     */
    @GetMapping("/artist/followers")
    public ResponseEntity<?> getMyFollowers(@LoginUser(artistOnly = true) SessionUser user) {
        List<FollowDTO.Follower> followers = followService.getArtistFollowers(user.getId());

        return ResponseEntity.ok(ApiResponse.ok(followers));
    }

    /**
     * 특정 아티스트에 대한 팔로우(구독)를 취소(언팔로우)합니다.
     *
     * @param artistId 팔로우를 취소할 아티스트의 고유 식별자
     * @param user     현재 인가된 사용자의 세션 객체
     * @return 처리 결과 메시지가 포함된 표준 응답 객체
     */
    @DeleteMapping("/{artistId}")
    public ResponseEntity<?> unfollowArtist(
            @PathVariable("artistId") Long artistId,
            @LoginUser SessionUser user
    ) {
        followService.setSubscription(artistId, user.getId(), false);

        return ResponseEntity.ok(ApiResponse.ok("팔로우가 취소되었습니다."));
    }
}