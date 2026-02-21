package com.app.onestepback.web.api.v1.membership;

import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.domain.dto.membership.MembershipDTO;
import com.app.onestepback.domain.dto.membership.MembershipManageDTO;
import com.app.onestepback.global.annotation.LoginUser;
import com.app.onestepback.global.common.response.ApiResponse;
import com.app.onestepback.service.membership.MembershipService;
import com.app.onestepback.web.api.v1.membership.request.MembershipRegisterRequest;
import com.app.onestepback.web.api.v1.membership.request.MembershipStatusUpdateRequest;
import com.app.onestepback.web.api.v1.membership.request.MembershipUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/memberships")
public class MembershipApiV1 {

    private final MembershipService membershipService;

    /**
     * 특정 아티스트가 운영 중인 멤버십 상품 목록을 조회합니다.
     *
     * @param artistId 조회할 아티스트의 고유 식별자
     * @return 아티스트의 멤버십 목록 데이터가 포함된 표준 응답 객체
     */
    @GetMapping
    public ResponseEntity<?> getMemberships(
            @RequestParam(name = "artist-id") Long artistId
    ) {
        List<MembershipDTO> memberships = membershipService.getMemberships(artistId, null);
        return ResponseEntity.ok(ApiResponse.ok(memberships));
    }

    /**
     * 아티스트 본인의 멤버십 관리 대시보드 정보를 조회합니다.
     *
     * @param user 현재 인가된 아티스트의 세션 객체
     * @return 정산 계좌 등록 여부 및 멤버십 목록이 포함된 DTO
     */
    @GetMapping("/manage")
    public ResponseEntity<?> manageMembership(
            @LoginUser(artistOnly = true) SessionUser user
    ) {
        MembershipManageDTO info = membershipService.getMembershipManageInfo(user.getId());
        return ResponseEntity.ok(info);
    }

    /**
     * 신규 멤버십 상품을 시스템에 등록합니다.
     *
     * @param user 현재 인가된 아티스트의 세션 객체
     * @param body 신규 등록할 멤버십 메타데이터 및 이미지 정보가 담긴 요청 객체
     * @return 상태 코드 204 (No Content)
     */
    @PostMapping
    public ResponseEntity<?> addMembership(
            @LoginUser(artistOnly = true) SessionUser user,
            @Valid @RequestBody MembershipRegisterRequest body
    ) {
        membershipService.registerMembership(body.toCmd(user.getId()));
        return ResponseEntity.noContent().build();
    }

    /**
     * 특정 멤버십 상품의 노출(활성화) 상태를 변경합니다.
     *
     * @param user         현재 인가된 아티스트의 세션 객체 (권한 검증용)
     * @param membershipId 상태를 변경할 멤버십의 고유 식별자
     * @param request      적용할 활성화 상태 (true: 노출, false: 숨김)
     * @return 상태 코드 204 (No Content)
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @LoginUser(artistOnly = true) SessionUser user,
            @PathVariable("id") Long membershipId,
            @RequestBody MembershipStatusUpdateRequest request
    ) {
        membershipService.updateStatus(user.getId(), membershipId, request.active());
        return ResponseEntity.noContent().build();
    }

    /**
     * 기존에 등록된 멤버십 상품의 세부 정보 및 이미지를 수정합니다.
     *
     * @param user         현재 인가된 아티스트의 세션 객체 (권한 검증용)
     * @param membershipId 수정할 멤버십의 고유 식별자
     * @param request      수정할 멤버십 데이터가 포함된 요청 객체
     * @return 상태 코드 204 (No Content)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMembership(
            @LoginUser(artistOnly = true) SessionUser user,
            @PathVariable("id") Long membershipId,
            @Valid @RequestBody MembershipUpdateRequest request
    ) {
        membershipService.update(request.toCmd(membershipId, user.getId()));
        return ResponseEntity.noContent().build();
    }

    /**
     * 특정 멤버십 상품을 영구적으로 삭제합니다.
     * <p>
     * 해당 멤버십을 이용 중인 활성 구독자가 존재할 경우 삭제 요청은 거부됩니다.
     * </p>
     *
     * @param user         현재 인가된 아티스트의 세션 객체 (권한 검증용)
     * @param membershipId 삭제할 멤버십의 고유 식별자
     * @return 상태 코드 204 (No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMembership(
            @LoginUser(artistOnly = true) SessionUser user,
            @PathVariable("id") Long membershipId
    ) {
        membershipService.delete(user.getId(), membershipId);
        return ResponseEntity.noContent().build();
    }
}