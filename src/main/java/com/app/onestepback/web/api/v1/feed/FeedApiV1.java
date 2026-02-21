package com.app.onestepback.web.api.v1.feed;

import com.app.onestepback.domain.dto.feed.FeedListDTO;
import com.app.onestepback.domain.dto.feed.FeedSearchCond;
import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.domain.type.feed.FeedCategory;
import com.app.onestepback.global.annotation.LoginUser;
import com.app.onestepback.global.common.Slice;
import com.app.onestepback.global.common.response.ApiResponse;
import com.app.onestepback.service.feed.FeedService;
import com.app.onestepback.service.feed.FeedLikeService;
import com.app.onestepback.web.api.v1.feed.request.FeedUpdateRequest;
import com.app.onestepback.web.api.v1.feed.request.FeedWriteRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/feeds")
@RequiredArgsConstructor
public class FeedApiV1 {

    private final FeedService feedService;
    private final FeedLikeService feedLikeService;

    /**
     * @description 인가된 아티스트 권한으로 새로운 피드(게시글)를 작성합니다.
     *
     * @param user 현재 인가된 아티스트의 세션 객체
     * @param body 피드 작성에 필요한 메타데이터와 파일 정보가 담긴 요청 객체
     * @return 비즈니스 로직 성공 여부가 포함된 표준 응답 객체
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> writeFeed(
            @LoginUser(artistOnly = true) SessionUser user,
            @RequestBody @Valid FeedWriteRequest body
    ) {
        feedService.write(body.toCmd(user.getId()));
        return ResponseEntity.ok(ApiResponse.ok());
    }

    /**
     * @description 기존에 작성된 피드의 내용을 수정합니다.
     *
     * @param user   현재 인가된 아티스트의 세션 객체 (소유권 검증용)
     * @param body   피드 수정에 필요한 데이터가 담긴 요청 객체
     * @param feedId 수정할 피드의 고유 식별자
     * @return 비즈니스 로직 성공 여부가 포함된 표준 응답 객체
     */
    @PutMapping("/{feedId}")
    public ResponseEntity<ApiResponse<Void>> updateFeed(
            @LoginUser(artistOnly = true) SessionUser user,
            @RequestBody @Valid FeedUpdateRequest body,
            @PathVariable("feedId") Long feedId
    ) {
        feedService.modify(body.toCmd(feedId, user.getId()));
        return ResponseEntity.ok(ApiResponse.ok());
    }

    /**
     * @description 주어진 조건에 맞는 피드 목록을 무한 스크롤(Slice) 방식으로 조회합니다.
     *
     * @param user       현재 인가된 사용자의 세션 객체 (열람 권한 확인용, 비로그인 허용)
     * @param artistId   특정 아티스트의 피드만 조회할 경우의 대상 식별자
     * @param category   조회할 피드의 카테고리 필터링 조건
     * @param lastFeedId 이전 페이지의 마지막 피드 식별자 (커서 기반 페이징)
     * @param size       조회할 데이터의 최대 개수 (기본값: 10)
     * @return 피드 목록 데이터가 포함된 표준 응답 객체
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Slice<FeedListDTO>>> getFeedList(
            @LoginUser(required = false) SessionUser user,
            @RequestParam(required = false) Long artistId,
            @RequestParam(required = false) FeedCategory category,
            @RequestParam(required = false) Long lastFeedId,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long viewerId = (user != null) ? user.getId() : null;

        FeedSearchCond cond = FeedSearchCond.builder()
                .artistId(artistId)
                .category(category)
                .viewerId(viewerId)
                .lastFeedId(lastFeedId)
                .size(size)
                .build();

        Slice<FeedListDTO> result = feedService.getList(cond);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /**
     * @description 특정 피드에 대한 사용자의 좋아요 상태를 토글(설정/해제)합니다.
     *
     * @param feedId 상태를 변경할 피드의 고유 식별자
     * @param status 적용할 좋아요 상태 (true: 추가, false: 취소)
     * @param user   현재 인가된 사용자의 세션 객체
     * @return 비즈니스 로직 성공 여부가 포함된 표준 응답 객체
     */
    @PostMapping("/like")
    public ResponseEntity<ApiResponse<Void>> setLike(
            @RequestParam("feed-id") Long feedId,
            @RequestParam("status") boolean status,
            @LoginUser SessionUser user
    ) {
        feedLikeService.setLike(feedId, user.getId(), status);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    /**
     * @description 작성한 피드를 논리적으로 삭제(Soft Delete) 처리합니다.
     *
     * @param feedId 삭제할 피드의 고유 식별자
     * @param user   현재 인가된 아티스트의 세션 객체 (소유권 검증용)
     * @return 비즈니스 로직 성공 여부가 포함된 표준 응답 객체
     */
    @DeleteMapping("/{feedId}")
    public ResponseEntity<ApiResponse<Void>> softDeleteFeed(
            @PathVariable("feedId") Long feedId,
            @LoginUser(artistOnly = true) SessionUser user
    ) {
        feedService.softDelete(feedId, user.getId());
        return ResponseEntity.ok(ApiResponse.ok());
    }
}