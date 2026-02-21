package com.app.onestepback.web.api.v1.artist;

import com.app.onestepback.domain.dto.artist.ArtistDTO;
import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.global.annotation.LoginUser;
import com.app.onestepback.global.common.response.ApiResponse;
import com.app.onestepback.global.exception.BusinessException;
import com.app.onestepback.global.exception.ErrorCode;
import com.app.onestepback.service.artist.ArtistService;
import com.app.onestepback.service.follow.FollowService;
import com.app.onestepback.web.api.v1.artist.request.ArtistRegisterRequest;
import com.app.onestepback.web.api.v1.artist.request.ArtistUpdateRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
public class ArtistApiV1 {

    private final FollowService followService;
    private final ArtistService artistService;

    /**
     * 신규 아티스트 등록을 처리하고, 등록 완료 후 현재 세션의 권한 상태를 갱신합니다.
     *
     * @param user                  현재 인가된 사용자의 세션 객체
     * @param artistRegisterRequest 클라이언트로부터 전달받은 아티스트 등록 데이터
     * @param session               권한 승격을 위한 현재 HTTP 세션
     * @return 등록이 완료된 아티스트의 고유 식별자가 포함된 표준 응답 객체
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerArtist(
            @LoginUser SessionUser user,
            @Valid @ModelAttribute ArtistRegisterRequest artistRegisterRequest,
            HttpSession session
    ) {
        Long artistId = artistService.saveArtist(artistRegisterRequest.toCmd(user.getId()));

        user.setArtist(true);
        session.setAttribute("member", user);

        return ResponseEntity.ok(ApiResponse.ok(artistId));
    }

    /**
     * 아티스트의 홈 화면 구성에 필요한 펀딩, 피드, 멤버십 통합 정보를 단일 조회합니다.
     *
     * @param artistId 조회를 요청받은 대상 아티스트의 고유 식별자
     * @return 아티스트 홈 탭 렌더링을 위한 종합 데이터 객체
     */
    @GetMapping("/{artistId}/home")
    public ResponseEntity<?> getArtistHome(@PathVariable("artistId") Long artistId) {
        var homeInfo = artistService.getArtistHomeInfo(artistId);

        return ResponseEntity.ok(ApiResponse.ok(homeInfo));
    }

    /**
     * 현재 로그인한 사용자가 구독 및 팔로우하고 있는 연결된 아티스트 목록을 반환합니다.
     *
     * @param user 현재 인가된 사용자의 세션 객체
     * @return 사용자와 연결된 구독/팔로우 아티스트 데이터 리스트
     */
    @GetMapping("/connected")
    public ResponseEntity<?> getConnectedArtists(
            @LoginUser SessionUser user
    ) {
        ArtistDTO.MyArtists myArtists = artistService.getMyArtists(user.getId());

        return ResponseEntity.ok(ApiResponse.ok(myArtists));
    }

    /**
     * 기존에 등록된 아티스트의 프로필 및 상세 정보를 수정하고 세션을 동기화합니다.
     *
     * @param user    현재 인가된 사용자의 세션 객체
     * @param body    수정할 내용이 담긴 아티스트 정보 커맨드 객체
     * @param session 정보 동기화를 위한 현재 HTTP 세션
     * @return 상태 코드 204 (No Content)
     */
    @PutMapping
    public ResponseEntity<Void> updateArtist(
            @LoginUser SessionUser user,
            @Valid @ModelAttribute("artistUpdateRequest") ArtistUpdateRequest body,
            HttpSession session
    ) {
        SessionUser updatedSessionUser = artistService.updateArtist(body.toCmd(user.getId()));

        session.setAttribute("member", updatedSessionUser);

        return ResponseEntity.noContent().build();
    }

    /**
     * 특정 아티스트에 대한 팔로우 상태를 활성화 또는 비활성화합니다.
     * <p>
     * 사용자가 본인 자신을 팔로우하려는 시도는 시스템 정책상 차단됩니다.
     * </p>
     *
     * @param artistId 대상 아티스트의 고유 식별자
     * @param status   적용할 팔로우 상태 (true: 팔로우, false: 언팔로우)
     * @param user     현재 인가된 사용자의 세션 객체
     * @return 상태 코드 204 (No Content)
     */
    @PostMapping("/follow")
    public ResponseEntity<Void> setSubscription(
            @RequestParam("artistId") Long artistId,
            @RequestParam("status") boolean status,
            @LoginUser SessionUser user
    ) {
        if (user.getId().equals(artistId)) {
            throw new BusinessException(ErrorCode.SELF_FOLLOW_NOT_ALLOWED);
        }

        followService.setSubscription(artistId, user.getId(), status);

        return ResponseEntity.noContent().build();
    }
}