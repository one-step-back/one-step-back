package com.app.onestepback.service.subscription;

import com.app.onestepback.domain.dto.subscription.SubscriptionDTO;
import com.app.onestepback.web.api.v1.subscription.request.SubscriptionRequest;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 아티스트 멤버십 구독(Subscription)의 전반적인 생명주기를 관리하는 비즈니스 인터페이스입니다.
 * <p>
 * 신규 구독, 업그레이드, 다운그레이드 예약, 월간 자동 갱신 및 결제 수단 변경 등의 핵심 로직을 제공합니다.
 * </p>
 */
public interface SubscriptionService {

    /**
     * 특정 아티스트의 멤버십에 신규 구독 결제를 수행하고 데이터를 생성합니다.
     *
     * @param memberId        구독을 요청한 회원의 고유 식별자
     * @param membershipId    가입할 멤버십 상품의 고유 식별자
     * @param paymentMethodId 결제에 사용할 수단(빌링키)의 고유 식별자
     * @return 구독이 완료된 아티스트의 고유 식별자
     */
    Long subscribe(Long memberId, Long membershipId, Long paymentMethodId);

    /**
     * 현재 구독 중인 멤버십을 상위 등급으로 즉시 업그레이드하고 차액 결제를 수행합니다.
     *
     * @param memberId 구독자의 고유 식별자
     * @param subId    현재 활성화된 구독 정보의 식별자
     * @param req      업그레이드 요청 정보(목표 멤버십, 결제 금액 등)가 포함된 DTO
     * @return 업그레이드가 완료된 아티스트의 고유 식별자
     */
    Long upgrade(Long memberId, Long subId, SubscriptionRequest.Upgrade req);

    /**
     * 현재 활성화된 구독을 하위 등급으로 변경하도록 다음 결제일에 예약합니다.
     *
     * @param memberId         구독자의 고유 식별자
     * @param subId            현재 활성화된 구독 정보의 식별자
     * @param nextMembershipId 다음 결제일에 적용될 하위 멤버십 식별자
     */
    void downgrade(Long memberId, Long subId, Long nextMembershipId);

    /**
     * 특정 회원의 특정 아티스트에 대한 가장 최근 구독 결제 영수증 내역을 조회합니다.
     *
     * @param memberId 구독자의 고유 식별자
     * @param artistId 아티스트의 고유 식별자
     * @return 구독 영수증 상세 정보 DTO
     */
    SubscriptionDTO.Receipt getLatestSubscription(Long memberId, Long artistId);

    /**
     * 특정 아티스트에 대해 현재 활성화된(ACTIVE) 구독 정보를 조회합니다.
     *
     * @param memberId 구독자의 고유 식별자
     * @param artistId 아티스트의 고유 식별자
     * @return 활성 구독 정보 DTO (구독 중이 아닐 경우 null 반환)
     */
    @Nullable
    SubscriptionDTO.Info findActiveSubscription(Long memberId, Long artistId);

    /**
     * 회원이 현재 구독 중인 모든 멤버십 관리 목록을 조회합니다.
     *
     * @param memberId 조회를 요청한 회원의 고유 식별자
     * @return 활성 구독 관리 정보 DTO 리스트
     */
    List<SubscriptionDTO.Management> findActiveSubscriptions(Long memberId);

    /**
     * 배치 스케줄러를 통해 다음 결제일이 도래한 구독의 정기 갱신 결제를 수행합니다.
     *
     * @param sub 갱신 처리를 진행할 구독 갱신 전용 DTO
     */
    void renewSubscription(SubscriptionDTO.Renewal sub);

    /**
     * 정기 갱신 결제 실패 시 해당 구독의 상태를 변경하고 실패 알림을 발송합니다.
     *
     * @param sub 결제에 실패한 구독 갱신 전용 DTO
     */
    void handleRenewalFailure(SubscriptionDTO.Renewal sub);

    /**
     * 구독의 다음 달 자동 결제(갱신) 여부를 설정하거나 해지 예약 처리합니다.
     *
     * @param memberId       구독자의 고유 식별자
     * @param subscriptionId 상태를 변경할 구독의 고유 식별자
     * @param autoRenewal    자동 갱신 활성화 여부
     */
    void updateAutoRenewal(Long memberId, Long subscriptionId, boolean autoRenewal);

    /**
     * 정기 결제에 사용될 카드를 다른 결제 수단으로 변경합니다.
     *
     * @param memberId        구독자의 고유 식별자
     * @param subscriptionId  결제 수단을 변경할 구독의 고유 식별자
     * @param paymentMethodId 새롭게 적용할 결제 수단의 고유 식별자
     */
    void updatePaymentMethod(Long memberId, Long subscriptionId, Long paymentMethodId);

    /**
     * 특정 아티스트를 구독 중인 회원 목록을 조회합니다. (아티스트 관리용)
     *
     * @param artistId 대상 아티스트의 고유 식별자
     * @return 구독자 리스트 뷰 DTO 목록
     */
    List<SubscriptionDTO.SubscriberList> getArtistSubscribers(Long artistId);
}