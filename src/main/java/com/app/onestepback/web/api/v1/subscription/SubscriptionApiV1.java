package com.app.onestepback.web.api.v1.subscription;

import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.domain.dto.subscription.SubscriptionDTO;
import com.app.onestepback.global.annotation.LoginUser;
import com.app.onestepback.global.common.response.ApiResponse;
import com.app.onestepback.service.subscription.SubscriptionService;
import com.app.onestepback.web.api.v1.subscription.request.SubscriptionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/subscription")
@RequiredArgsConstructor
public class SubscriptionApiV1 {

    private final SubscriptionService subscriptionService;

    /**
     * @description 특정 아티스트의 멤버십에 신규 구독을 결제하고 활성화합니다.
     *
     * @param user    현재 인가된 사용자의 세션 객체
     * @param request 구독할 멤버십 및 결제 수단 정보가 포함된 요청 객체
     * @return 구독 성공 후 이동할 리다이렉트 URL 정보가 포함된 표준 응답 객체
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> subscribe(
            @LoginUser SessionUser user,
            @Valid @RequestBody SubscriptionRequest.Create request
    ) {
        Long artistId = subscriptionService.subscribe(user.getId(), request.membershipId(), request.paymentMethodId());
        return ResponseEntity.ok(ApiResponse.ok(Map.of("redirectUrl", "/artist/" + artistId + "/membership/success")));
    }

    /**
     * @description 현재 구독 중인 멤버십을 상위 등급으로 업그레이드하고 차액을 즉시 결제합니다.
     *
     * @param user    현재 인가된 사용자의 세션 객체
     * @param subId   대상 구독 정보의 고유 식별자
     * @param request 업그레이드할 대상 멤버십 및 결제 금액 정보가 포함된 요청 객체
     * @return 결제 성공 후 이동할 리다이렉트 URL 정보가 포함된 표준 응답 객체
     */
    @PostMapping("/{subId}/upgrade")
    public ResponseEntity<ApiResponse<Map<String, String>>> upgrade(
            @LoginUser SessionUser user,
            @PathVariable("subId") Long subId,
            @Valid @RequestBody SubscriptionRequest.Upgrade request
    ) {
        Long artistId = subscriptionService.upgrade(user.getId(), subId, request);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("redirectUrl", "/artist/" + artistId + "/membership/success")));
    }

    /**
     * @description 다음 결제일에 하위 멤버십 등급으로 갱신되도록 다운그레이드를 예약합니다.
     *
     * @param user    현재 인가된 사용자의 세션 객체
     * @param subId   대상 구독 정보의 고유 식별자
     * @param request 다운그레이드할 대상 멤버십 정보가 포함된 요청 객체
     * @return 처리 결과 메시지가 포함된 표준 응답 객체
     */
    @PatchMapping("/{subId}/downgrade")
    public ResponseEntity<ApiResponse<String>> downgrade(
            @LoginUser SessionUser user,
            @PathVariable("subId") Long subId,
            @Valid @RequestBody SubscriptionRequest.Downgrade request
    ) {
        subscriptionService.downgrade(user.getId(), subId, request.nextMembershipId());
        return ResponseEntity.ok(ApiResponse.ok("다운그레이드 예약 완료"));
    }

    /**
     * @description 구독의 월간 자동 갱신(결제) 여부를 활성화 또는 비활성화 처리합니다.
     *
     * @param user           현재 인가된 사용자의 세션 객체
     * @param subscriptionId 상태를 변경할 구독의 고유 식별자
     * @param autoRenewal    자동 갱신 활성화 여부 (true: 유지, false: 해지 예약)
     * @return 비즈니스 로직 성공 여부가 포함된 표준 응답 객체
     */
    @PatchMapping("/{subscriptionId}/auto-renewal")
    public ResponseEntity<ApiResponse<Void>> updateAutoRenewal(
            @LoginUser SessionUser user,
            @PathVariable("subscriptionId") Long subscriptionId,
            @RequestParam("autoRenewal") boolean autoRenewal
    ) {
        subscriptionService.updateAutoRenewal(user.getId(), subscriptionId, autoRenewal);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    /**
     * @description 정기 결제에 사용될 빌링키(결제 수단)를 다른 수단으로 변경합니다.
     *
     * @param user            현재 인가된 사용자의 세션 객체
     * @param subscriptionId  결제 수단을 변경할 구독의 고유 식별자
     * @param paymentMethodId 새롭게 적용할 결제 수단의 고유 식별자
     * @return 비즈니스 로직 성공 여부가 포함된 표준 응답 객체
     */
    @PatchMapping("/{subscriptionId}/payment-method")
    public ResponseEntity<ApiResponse<Void>> updatePaymentMethod(
            @LoginUser SessionUser user,
            @PathVariable("subscriptionId") Long subscriptionId,
            @RequestParam("paymentMethodId") Long paymentMethodId
    ) {
        subscriptionService.updatePaymentMethod(user.getId(), subscriptionId, paymentMethodId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    /**
     * @description 아티스트 권한으로 자신을 구독하고 있는 활성 구독자(팬) 목록을 조회합니다.
     *
     * @param user 현재 인가된 아티스트의 세션 객체
     * @return 구독자 목록 데이터가 포함된 표준 응답 객체
     */
    @GetMapping("/artist/subscribers")
    public ResponseEntity<ApiResponse<List<SubscriptionDTO.SubscriberList>>> getMySubscribers(
            @LoginUser(artistOnly = true) SessionUser user
    ) {
        List<SubscriptionDTO.SubscriberList> subscribers = subscriptionService.getArtistSubscribers(user.getId());
        return ResponseEntity.ok(ApiResponse.ok(subscribers));
    }
}