package com.app.onestepback.service.settlement;

import com.app.onestepback.domain.dto.artist.ArtistDTO;
import com.app.onestepback.domain.dto.settlement.SettlementDTO;
import com.app.onestepback.domain.model.SettlementVO;
import com.app.onestepback.domain.type.notification.NotificationType;
import com.app.onestepback.domain.type.settlement.SettlementStatus;
import com.app.onestepback.global.client.PortOneClient;
import com.app.onestepback.global.common.Slice;
import com.app.onestepback.global.exception.BusinessException;
import com.app.onestepback.global.exception.ErrorCode;
import com.app.onestepback.repository.ArtistMapper;
import com.app.onestepback.repository.PaymentHistoryMapper;
import com.app.onestepback.repository.SettlementMapper;
import com.app.onestepback.service.notification.event.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * 수익 정산 처리를 수행하는 핵심 비즈니스 로직 구현체입니다.
 * <p>
 * 외부 송금 API와의 연동 시 발생할 수 있는 장애 상황을 대비하여,
 * 독립적인 보상 트랜잭션(REQUIRES_NEW)을 통한 데이터 롤백 및 상태 동기화를 보장합니다.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {

    private static final double FEE_RATE = 0.05;

    private final SettlementMapper settlementMapper;
    private final PaymentHistoryMapper paymentHistoryMapper;
    private final ArtistMapper artistMapper;

    private final PortOneClient portOneClient;
    private final NotificationPublisher notificationPublisher;

    @Override
    @Transactional
    public void requestSettlement(Long artistId) {
        Long totalUnsettled = settlementMapper.selectUnsettledTotalAmount(artistId);

        if (totalUnsettled == null || totalUnsettled <= 0) {
            throw new BusinessException(ErrorCode.SETTLEMENT_NO_AMOUNT);
        }

        Long fee = (long) (totalUnsettled * FEE_RATE);
        Long netAmount = totalUnsettled - fee;

        SettlementVO settlementVO = SettlementVO.builder()
                .artistId(artistId)
                .totalAmount(totalUnsettled)
                .feeAmount(fee)
                .netAmount(netAmount)
                .status(SettlementStatus.REQUESTED)
                .build();

        settlementMapper.insertSettlement(settlementVO);

        int updatedRows = paymentHistoryMapper.updateSettlementId(artistId, settlementVO.getId());

        if (updatedRows == 0) {
            throw new BusinessException(ErrorCode.SETTLEMENT_PAYMENT_NOT_FOUND);
        }

        log.info("[Settlement Request] 아티스트 정산 신청 완료 - ArtistID: {}, NetAmount: {}, UpdatedRows: {}",
                artistId, netAmount, updatedRows);
    }

    @Override
    @Transactional(readOnly = true)
    public SettlementDTO.Dashboard getSettlementDashboard(Long artistId) {
        Long totalUnsettled = settlementMapper.selectUnsettledTotalAmount(artistId);
        if (totalUnsettled == null) totalUnsettled = 0L;

        Long feeAmount = (long) (totalUnsettled * FEE_RATE);
        Long distributableAmount = totalUnsettled - feeAmount;

        Long completedAmount = settlementMapper.selectCompletedTotalAmount(artistId);
        if (completedAmount == null) completedAmount = 0L;

        SettlementDTO.Summary summary = new SettlementDTO.Summary(
                artistId,
                totalUnsettled,
                feeAmount,
                distributableAmount,
                completedAmount
        );

        List<SettlementDTO.MonthlyRevenue> monthlyRevenues = settlementMapper.selectMonthlyRevenue(artistId);

        return new SettlementDTO.Dashboard(summary, monthlyRevenues);
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<SettlementDTO.History> getSettlementHistory(Long artistId, Long lastSettlementId) {
        int size = 10;
        List<SettlementDTO.History> content = settlementMapper.selectSettlementHistory(
                artistId,
                lastSettlementId,
                size + 1
        );

        return new Slice<>(content, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SettlementVO> findRequestedSettlements() {
        return settlementMapper.selectRequestedSettlements();
    }

    @Override
    @Transactional
    public void processSettlement(SettlementVO settlement) {
        Long id = settlement.getId();
        Long artistId = settlement.getArtistId();

        ArtistDTO.BankInfo bankInfo = artistMapper.selectBankInfo(artistId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTIST_NOT_FOUND));

        if (!"ACTIVE".equals(bankInfo.memberStatus())) {
            throw new BusinessException(ErrorCode.SETTLEMENT_ARTIST_INACTIVE);
        }
        if (bankInfo.bankName() == null || bankInfo.accountNumber() == null) {
            throw new BusinessException(ErrorCode.SETTLEMENT_BANK_INFO_REQUIRED);
        }

        portOneClient.transferMoney(
                bankInfo.bankName(),
                bankInfo.accountNumber(),
                bankInfo.accountHolder(),
                settlement.getNetAmount()
        );

        log.info("[Payout] 송금 처리 완료: ID={} / 은행={} / 금액={}원",
                id, bankInfo.bankName().getDescription(), settlement.getNetAmount());

        settlementMapper.updateSettlementStatus(id, SettlementStatus.COMPLETED);

        sendNotification(artistId, NotificationType.SETTLEMENT_COMPLETED, settlement.getNetAmount(), null);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleSettlementFailure(SettlementVO settlement, String reason) {
        log.warn("[Reject] 정산 반려 처리 - ID: {}, 사유: {}", settlement.getId(), reason);

        settlementMapper.updateSettlementStatus(settlement.getId(), SettlementStatus.REJECTED);
        paymentHistoryMapper.updateSettlementIdToNull(settlement.getId());

        sendNotification(settlement.getArtistId(), NotificationType.SETTLEMENT_REJECTED, null, reason);
    }

    /**
     * 정산 상태 변경에 따른 시스템 알림을 포맷팅하여 발행합니다.
     */
    private void sendNotification(Long artistId, NotificationType type, Long amount, String reason) {
        String message;
        if (type == NotificationType.SETTLEMENT_COMPLETED && amount != null) {
            String formattedAmount = NumberFormat.getNumberInstance(Locale.KOREA).format(amount);
            message = String.format("정산금 %s원이 지급되었습니다.", formattedAmount);
        } else {
            message = String.format("정산 신청이 반려되었습니다: %s", reason);
        }

        notificationPublisher.send(
                null,
                artistId,
                type,
                "/artist/dashboard/settlement",
                message
        );
    }
}