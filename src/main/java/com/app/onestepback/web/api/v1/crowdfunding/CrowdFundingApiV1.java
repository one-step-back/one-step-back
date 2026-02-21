package com.app.onestepback.web.api.v1.crowdfunding;

import com.app.onestepback.domain.dto.crowdfunding.CrowdFundingDTO;
import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.global.annotation.LoginUser;
import com.app.onestepback.global.common.response.ApiResponse;
import com.app.onestepback.service.crowdfunding.CrowdFundingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/crowd-funding")
@RequiredArgsConstructor
public class CrowdFundingApiV1 {

    private final CrowdFundingService crowdFundingService;

    /**
     * 신규 크라우드 펀딩 프로젝트를 생성하거나 아티스트에게 제안합니다.
     * <p>
     * 파일 업로드가 선행되어 유효한 파일 식별자(fileId)를 포함해야 합니다.
     * </p>
     *
     * @param user    현재 인가된 사용자의 세션 객체
     * @param request 펀딩 생성에 필요한 메타데이터가 포함된 요청 객체
     * @return 생성된 펀딩 프로젝트의 고유 식별자가 포함된 표준 응답 객체
     */
    @PostMapping
    public ResponseEntity<?> create(
            @LoginUser SessionUser user,
            @RequestBody @Valid CrowdFundingRequest.Create request
    ) {
        var command = request.toCommand(user.getId());
        Long fundingId = crowdFundingService.create(command, null);

        return ResponseEntity.ok(ApiResponse.ok(fundingId));
    }

    /**
     * 조건에 일치하는 크라우드 펀딩 프로젝트 목록을 조회합니다.
     * <p>
     * 아티스트 식별자가 제공된 경우 해당 아티스트의 펀딩 목록을,
     * 그렇지 않은 경우 상태값에 따른 전체 공개 펀딩 목록을 반환합니다.
     * </p>
     *
     * @param artistId 조회할 아티스트의 고유 식별자 (선택)
     * @param status   조회할 펀딩의 상태 필터링 조건 (기본값: ALL)
     * @return 펀딩 프로젝트 목록 데이터가 포함된 표준 응답 객체
     */
    @GetMapping("/list")
    public ResponseEntity<?> getList(
            @RequestParam(value = "artistId", required = false) Long artistId,
            @RequestParam(value = "status", required = false, defaultValue = "ALL") String status
    ) {
        if (artistId != null) {
            List<CrowdFundingDTO.ListView> list = crowdFundingService.getList(artistId);
            return ResponseEntity.ok(ApiResponse.ok(list));
        } else {
            List<CrowdFundingDTO.PublicList> list = crowdFundingService.getFundingList(status);
            return ResponseEntity.ok(ApiResponse.ok(list));
        }
    }

    /**
     * 진행 중인 특정 크라우드 펀딩 프로젝트에 후원 결제를 수행합니다.
     *
     * @param user      현재 인가된 사용자의 세션 객체
     * @param request   결제 수단 및 금액 정보가 포함된 요청 객체
     * @param fundingId 후원할 펀딩 프로젝트의 고유 식별자
     * @return 처리된 후원 결제 내역의 고유 식별자가 포함된 표준 응답 객체
     */
    @PostMapping("/fund/{fundingId}")
    public ResponseEntity<?> fund(
            @LoginUser SessionUser user,
            @RequestBody @Valid CrowdFundingRequest.PaymentRequest request,
            @PathVariable("fundingId") Long fundingId
    ) {
        Long fundingPaymentId = crowdFundingService.fund(fundingId, user.getId(), request.paymentMethodId(), request.amount());

        return ResponseEntity.ok(ApiResponse.ok(fundingPaymentId));
    }

    /**
     * 제안받은 크라우드 펀딩 프로젝트를 반려 처리합니다.
     *
     * @param user      현재 인가된 아티스트의 세션 객체
     * @param fundingId 반려할 펀딩 프로젝트의 고유 식별자
     * @param request   반려 사유가 포함된 요청 객체
     * @return 상태 코드 200 (OK)
     */
    @PatchMapping("/{fundingId}/reject")
    public ResponseEntity<?> reject(
            @LoginUser(artistOnly = true) SessionUser user,
            @PathVariable("fundingId") Long fundingId,
            @RequestBody @Valid CrowdFundingRequest.Reject request
    ) {
        crowdFundingService.reject(fundingId, user.getId(), request.rejectReason());

        return ResponseEntity.ok(ApiResponse.ok());
    }

    /**
     * 제안받은 크라우드 펀딩 프로젝트의 세부 내용을 수정하고 승낙(진행) 상태로 변경합니다.
     *
     * @param user      현재 인가된 아티스트의 세션 객체
     * @param fundingId 승낙할 펀딩 프로젝트의 고유 식별자
     * @param request   수정 및 오픈에 필요한 데이터가 포함된 요청 객체
     * @return 상태 코드 200 (OK)
     */
    @PatchMapping("/{fundingId}/edit-accept")
    public ResponseEntity<?> editAndAccept(
            @LoginUser(artistOnly = true) SessionUser user,
            @PathVariable("fundingId") Long fundingId,
            @RequestBody @Valid CrowdFundingRequest.EditAccept request
    ) {
        var command = request.toCommand(fundingId, user.getId());
        crowdFundingService.editAndAccept(command);

        return ResponseEntity.ok(ApiResponse.ok());
    }

    /**
     * 종료된 크라우드 펀딩 프로젝트의 상태를 최종 성공으로 변경하고 자산 이관 처리를 수행합니다.
     *
     * @param user      현재 인가된 아티스트의 세션 객체
     * @param fundingId 성공 처리할 펀딩 프로젝트의 고유 식별자
     * @return 상태 코드 200 (OK)
     */
    @PatchMapping("/{fundingId}/success")
    public ResponseEntity<?> success(
            @LoginUser(artistOnly = true) SessionUser user,
            @PathVariable("fundingId") Long fundingId
    ) {
        crowdFundingService.success(fundingId, user.getId());

        return ResponseEntity.ok(ApiResponse.ok());
    }

    /**
     * 종료된 크라우드 펀딩 프로젝트의 상태를 실패로 변경하고 참여자 전원의 결제를 일괄 취소(환불)합니다.
     *
     * @param user      현재 인가된 아티스트의 세션 객체
     * @param fundingId 실패 처리할 펀딩 프로젝트의 고유 식별자
     * @param request   실패 및 환불 사유가 포함된 요청 객체
     * @return 상태 코드 200 (OK)
     */
    @PatchMapping("/{fundingId}/fail")
    public ResponseEntity<?> fail(
            @LoginUser(artistOnly = true) SessionUser user,
            @PathVariable("fundingId") Long fundingId,
            @RequestBody @Valid CrowdFundingRequest.Reject request
    ) {
        crowdFundingService.fail(fundingId, user.getId(), request.rejectReason());

        return ResponseEntity.ok(ApiResponse.ok());
    }

    /**
     * 현재 로그인한 사용자의 크라우드 펀딩 후원 및 결제 내역을 무한 스크롤 방식으로 조회합니다.
     *
     * @param user          현재 인가된 사용자의 세션 객체
     * @param lastPaymentId 이전 페이지의 마지막 결제 식별자 (커서 기반 페이징)
     * @param size          조회할 데이터의 최대 개수 (기본값: 10)
     * @return 사용자의 후원 내역이 포함된 슬라이스(Slice) 응답 객체
     */
    @GetMapping("/my-history")
    public ResponseEntity<?> getMyFundingHistory(
            @LoginUser SessionUser user,
            @RequestParam(required = false) Long lastPaymentId,
            @RequestParam(defaultValue = "10") int size
    ) {
        var historySlice = crowdFundingService.getMyFundingHistory(user.getId(), lastPaymentId, size);

        return ResponseEntity.ok(ApiResponse.ok(historySlice));
    }
}