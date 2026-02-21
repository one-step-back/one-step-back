package com.app.onestepback.service.subscription;

import com.app.onestepback.domain.dto.payment.PaymentResultDTO;
import com.app.onestepback.domain.dto.subscription.SubscriptionDTO;
import com.app.onestepback.domain.model.MembershipVO;
import com.app.onestepback.domain.model.PaymentHistoryVO;
import com.app.onestepback.domain.model.PaymentMethodVO;
import com.app.onestepback.domain.model.SubscriptionVO;
import com.app.onestepback.domain.type.notification.NotificationType;
import com.app.onestepback.domain.type.subscribtion.SubscriptionStatus;
import com.app.onestepback.global.client.PortOneClient;
import com.app.onestepback.global.exception.BusinessException;
import com.app.onestepback.global.exception.ErrorCode;
import com.app.onestepback.repository.MembershipMapper;
import com.app.onestepback.repository.PaymentHistoryMapper;
import com.app.onestepback.repository.PaymentMethodMapper;
import com.app.onestepback.repository.SubscriptionMapper;
import com.app.onestepback.service.notification.event.NotificationPublisher;
import com.app.onestepback.web.api.v1.subscription.request.SubscriptionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 멤버십 구독(Subscription) 비즈니스 로직을 처리하는 구현체입니다.
 * <p>
 * 포트원(PortOne) 결제망과의 통신 중 발생하는 예외에 대응하여, 시스템 무결성을 유지하기 위한
 * 보상 트랜잭션(결제 취소 연동) 및 세밀한 검증을 수행합니다.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionMapper subscriptionMapper;
    private final PaymentMethodMapper paymentMethodMapper;
    private final PaymentHistoryMapper paymentHistoryMapper;
    private final MembershipMapper membershipMapper;

    private final PortOneClient portOneClient;
    private final NotificationPublisher notificationPublisher;

    @Override
    @Transactional
    @CacheEvict(value = "artistDetail", allEntries = true)
    public Long subscribe(Long memberId, Long membershipId, Long paymentMethodId) {
        MembershipVO membership = membershipMapper.selectById(membershipId).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBERSHIP_NOT_FOUND)
        );

        if (!membership.isActive()) {
            throw new BusinessException(ErrorCode.MEMBERSHIP_INACTIVE);
        }

        if (subscriptionMapper.existsByMemberAndArtist(memberId, membership.getArtistId())) {
            throw new BusinessException(ErrorCode.SUBSCRIPTION_ALREADY_EXISTS);
        }

        PaymentMethodVO paymentMethod = paymentMethodMapper.selectById(paymentMethodId).orElseThrow(
                () -> new BusinessException(ErrorCode.PAYMENT_METHOD_NOT_FOUND)
        );

        if (!paymentMethod.getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        String merchantUid = "sub_" + UUID.randomUUID();
        PaymentResultDTO paymentResult;

        try {
            paymentResult = portOneClient.payWithBillingKey(
                    paymentMethod.getBillingKey(),
                    merchantUid,
                    membership.getPrice(),
                    membership.getName()
            );
        } catch (Exception e) {
            log.error("[Subscription] 외부 결제망 승인 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.PAYMENT_FAILED);
        }

        try {
            SubscriptionVO subscription = SubscriptionVO.builder()
                    .memberId(memberId)
                    .membershipId(membershipId)
                    .lastMerchantUid(merchantUid)
                    .artistId(membership.getArtistId())
                    .status(SubscriptionStatus.ACTIVE)
                    .startDate(LocalDateTime.now())
                    .nextBillingDate(LocalDateTime.now().plusMonths(1))
                    .paymentMethodId(paymentMethodId)
                    .build();
            subscriptionMapper.insert(subscription);

            PaymentHistoryVO payment = PaymentHistoryVO.builder()
                    .memberId(memberId)
                    .subscriptionId(subscription.getId())
                    .merchantUid(merchantUid)
                    .impUid(paymentResult.getImpUid())
                    .amount(membership.getPrice())
                    .status("PAID")
                    .paymentType("SUBSCRIPTION")
                    .build();
            paymentHistoryMapper.insert(payment);

            return membership.getArtistId();

        } catch (Exception e) {
            log.error("[Subscription] 내부 데이터베이스 저장 실패로 인한 결제 자동 취소 연동. impUid: {}", paymentResult.getImpUid(), e);
            try {
                portOneClient.cancelPayment(
                        paymentResult.getImpUid(),
                        "시스템 내부 저장 오류로 인한 정기결제 자동 취소",
                        membership.getPrice()
                );
            } catch (Exception cancelError) {
                log.error("[Subscription] 결제 자동 취소 실패 (관리자 수동 환불 요망). impUid: {}", paymentResult.getImpUid(), cancelError);
            }
            throw new BusinessException(ErrorCode.PAYMENT_SYSTEM_ERROR);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "artistDetail", allEntries = true)
    public Long upgrade(Long memberId, Long subId, SubscriptionRequest.Upgrade req) {
        SubscriptionVO currentSub = subscriptionMapper.selectById(subId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

        if (!currentSub.getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        MembershipVO targetMembership = membershipMapper.selectById(req.newMembershipId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBERSHIP_NOT_FOUND));

        PaymentMethodVO paymentMethod = paymentMethodMapper.selectById(req.paymentMethodId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_METHOD_NOT_FOUND));

        if (!paymentMethod.getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        String merchantUid = "upg_" + UUID.randomUUID();
        PaymentResultDTO paymentResult = null;

        if (req.amount() > 0) {
            try {
                paymentResult = portOneClient.payWithBillingKey(
                        paymentMethod.getBillingKey(),
                        merchantUid,
                        req.amount(),
                        "[업그레이드] " + targetMembership.getName()
                );
            } catch (Exception e) {
                log.error("[Subscription Upgrade] 차액 결제망 승인 실패: {}", e.getMessage());
                throw new BusinessException(ErrorCode.PAYMENT_FAILED);
            }
        }

        try {
            SubscriptionVO updateVO = SubscriptionVO.builder()
                    .id(currentSub.getId())
                    .membershipId(targetMembership.getId())
                    .paymentMethodId(req.paymentMethodId())
                    .nextMembershipId(null)
                    .build();

            subscriptionMapper.update(updateVO);

            if (paymentResult != null) {
                PaymentHistoryVO history = PaymentHistoryVO.builder()
                        .memberId(memberId)
                        .subscriptionId(currentSub.getId())
                        .merchantUid(merchantUid)
                        .impUid(paymentResult.getImpUid())
                        .amount(req.amount())
                        .status("PAID")
                        .paymentType("UPGRADE")
                        .build();
                paymentHistoryMapper.insert(history);
            }

            return currentSub.getArtistId();

        } catch (Exception e) {
            if (paymentResult != null) {
                log.error("[Subscription Upgrade] DB 갱신 실패. 업그레이드 결제 자동 취소. impUid: {}", paymentResult.getImpUid(), e);
                try {
                    portOneClient.cancelPayment(
                            paymentResult.getImpUid(),
                            "시스템 오류로 인한 업그레이드 취소",
                            req.amount()
                    );
                } catch (Exception cancelError) {
                    log.error("[Subscription Upgrade] 업그레이드 결제 취소 실패 (관리자 수동 환불 요망).", cancelError);
                }
            }
            throw new BusinessException(ErrorCode.PAYMENT_SYSTEM_ERROR);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "artistDetail", allEntries = true)
    public void downgrade(Long memberId, Long subId, Long nextMembershipId) {
        SubscriptionVO currentSub = subscriptionMapper.selectById(subId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

        if (!currentSub.getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        if (!membershipMapper.existsById(nextMembershipId)) {
            throw new BusinessException(ErrorCode.MEMBERSHIP_NOT_FOUND);
        }

        SubscriptionVO updateVO = SubscriptionVO.builder()
                .id(currentSub.getId())
                .nextMembershipId(nextMembershipId)
                .build();

        subscriptionMapper.update(updateVO);

        log.info("[Subscription Downgrade] 다운그레이드 예약 갱신 완료 - SubID: {}, NextMembershipID: {}", subId, nextMembershipId);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionDTO.Receipt getLatestSubscription(Long memberId, Long artistId) {
        return subscriptionMapper.selectLatestSubscription(memberId, artistId).orElseThrow(
                () -> new BusinessException(ErrorCode.SUBSCRIPTION_NOT_FOUND)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionDTO.Info findActiveSubscription(Long memberId, Long artistId) {
        return subscriptionMapper.selectActiveSubscription(memberId, artistId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDTO.Management> findActiveSubscriptions(Long memberId) {
        return subscriptionMapper.selectActiveSubscriptionsByMemberId(memberId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void renewSubscription(SubscriptionDTO.Renewal sub) {
        if (!sub.autoRenewal()) {
            SubscriptionVO cancelVO = SubscriptionVO.builder()
                    .id(sub.id())
                    .status(SubscriptionStatus.CANCELLED)
                    .build();

            subscriptionMapper.update(cancelVO);

            log.info("[Subscription Expiration] 자동 갱신 미설정으로 인한 구독 만료 처리 완료 - SubID: {}", sub.id());
            return;
        }

        String merchantUid = "renew_" + UUID.randomUUID();
        Long targetPrice = sub.price();
        String targetName = sub.membershipName();
        Long targetMembershipId = null;
        boolean isChanged = false;

        if (sub.nextMembershipId() != null) {
            MembershipVO nextMembership = membershipMapper.selectById(sub.nextMembershipId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.MEMBERSHIP_NOT_FOUND));

            targetPrice = nextMembership.getPrice();
            targetName = nextMembership.getName();
            targetMembershipId = nextMembership.getId();
            isChanged = true;

            log.info("[Subscription Renewal] 예약된 멤버십 변경 감지 적용 - SubID: {}, To: {}", sub.id(), targetName);
        }

        PaymentResultDTO result = portOneClient.payWithBillingKey(
                sub.billingKey(),
                merchantUid,
                targetPrice,
                "[정기결제] " + targetName
        );

        PaymentHistoryVO history = PaymentHistoryVO.builder()
                .memberId(sub.memberId())
                .subscriptionId(sub.id())
                .merchantUid(merchantUid)
                .impUid(result.getImpUid())
                .amount(targetPrice)
                .status("PAID")
                .paymentType("RENEWAL")
                .build();
        paymentHistoryMapper.insert(history);

        SubscriptionVO renewalVO = SubscriptionVO.builder()
                .id(sub.id())
                .lastMerchantUid(merchantUid)
                .nextBillingDate(LocalDateTime.now().plusMonths(1))
                .membershipId(targetMembershipId)
                .build();

        subscriptionMapper.updateRenewalSuccess(renewalVO);

        NotificationType notiType = isChanged ? NotificationType.SUBSCRIPTION_UPDATE : NotificationType.SUBSCRIPTION_RENEWAL;

        notificationPublisher.send(
                sub.artistId(),
                sub.memberId(),
                notiType,
                "/my-page/subscription"
        );

        log.info("[Subscription Renewal] 자동 정기 결제 및 갱신 성공 - SubID: {}, Plan: {}", sub.id(), targetName);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleRenewalFailure(SubscriptionDTO.Renewal sub) {
        subscriptionMapper.updateStatusToFailed(sub.id());

        notificationPublisher.send(
                sub.artistId(),
                sub.memberId(),
                NotificationType.SUBSCRIPTION_FAILED,
                "/my-page/subscription"
        );

        log.warn("[Subscription Renewal] 정기 결제 실패로 인한 구독 상태 FAILED 변경 및 알림 발행 완료 - SubID: {}", sub.id());
    }

    @Override
    @Transactional
    public void updateAutoRenewal(Long memberId, Long subscriptionId, boolean autoRenewal) {
        SubscriptionVO subscription = subscriptionMapper.selectById(subscriptionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

        if (!subscription.getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        SubscriptionVO updateVO = SubscriptionVO.builder()
                .id(subscription.getId())
                .autoRenewal(autoRenewal)
                .build();

        subscriptionMapper.update(updateVO);

        log.info("[AutoRenewal Update] 구독 자동 갱신 상태 변경 완료 - SubID: {}, Status: {}", subscriptionId, autoRenewal);
    }

    @Override
    @Transactional
    public void updatePaymentMethod(Long memberId, Long subscriptionId, Long paymentMethodId) {
        SubscriptionVO subscription = subscriptionMapper.selectById(subscriptionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

        if (!subscription.getMemberId().equals(memberId)) {
            log.warn("[Security Alert] 타인의 구독 결제 수단 변경 시도 - User: {}, Sub: {}", memberId, subscriptionId);
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        PaymentMethodVO paymentMethod = paymentMethodMapper.selectById(paymentMethodId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_METHOD_NOT_FOUND));

        if (!paymentMethod.getMemberId().equals(memberId)) {
            log.warn("[Security Alert] 타인의 결제 수단 도용 시도 - User: {}, Card: {}", memberId, paymentMethodId);
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        SubscriptionVO updateVO = SubscriptionVO.builder()
                .id(subscription.getId())
                .paymentMethodId(paymentMethodId)
                .build();

        subscriptionMapper.update(updateVO);

        log.info("[PaymentMethod Update] 정기 결제 수단 갱신 검증 및 반영 완료 - SubID: {}, NewPaymentID: {}", subscriptionId, paymentMethodId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDTO.SubscriberList> getArtistSubscribers(Long artistId) {
        return subscriptionMapper.selectSubscribersByArtistId(artistId);
    }
}