package com.app.onestepback.repository;

import com.app.onestepback.domain.dto.subscription.SubscriptionDTO;
import com.app.onestepback.domain.model.SubscriptionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SubscriptionMapper {
    // [C] 구독 생성
    void insert(SubscriptionVO vo);

    // [R] ID로 구독 정보 단건 조회 (서비스 로직용)
    Optional<SubscriptionVO> selectById(Long id);

    // [R] 활성 구독자 수 카운트 (멤버십 삭제 방지용)
    int countActiveSubscribers(Long membershipId);

    // [R] 중복 구독 체크
    boolean existsByMemberAndArtist(@Param("memberId") Long memberId, @Param("artistId") Long artistId);

    // [R] 최신 구독 영수증 조회 (화면용)
    Optional<SubscriptionDTO.Receipt> selectLatestSubscription(@Param("memberId") Long memberId, @Param("artistId") Long artistId);

    // [R] 현재 활성 구독 정보 조회 (로직용 - 업그레이드/다운그레이드 판별)
    Optional<SubscriptionDTO.Info> selectActiveSubscription(
            @Param("memberId") Long memberId,
            @Param("artistId") Long artistId
    );

    // [R] 갱신 대상 구독 목록 조회 (스케줄러용)
    List<SubscriptionDTO.Renewal> selectSubscriptionsForRenewal();

    List<SubscriptionDTO.Management> selectActiveSubscriptionsByMemberId(@Param("memberId") Long memberId);

    List<SubscriptionDTO.SubscriberList> selectSubscribersByArtistId(Long artistId);

    // [U] 구독 정보 업데이트 (업그레이드/다운그레이드/상태변경 등)
    void update(SubscriptionVO vo);

    // [U] 갱신 성공 시 다음 결제일 업데이트
    void updateNextBillingDate(@Param("id") Long id, @Param("lastMerchantUid") String lastMerchantUid);

    // [U] 갱신 실패 시 상태 변경
    void updateStatusToFailed(Long id);

    void updateRenewalSuccess(SubscriptionVO vo);

    void updatePaymentMethodToNull(Long paymentMethodId);
}