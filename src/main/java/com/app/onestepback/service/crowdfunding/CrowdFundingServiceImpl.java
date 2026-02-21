package com.app.onestepback.service.crowdfunding;

import com.app.onestepback.domain.dto.crowdfunding.CrowdFundingDTO;
import com.app.onestepback.domain.dto.payment.FundingPaymentDTO;
import com.app.onestepback.domain.dto.payment.PaymentResultDTO;
import com.app.onestepback.domain.model.CrowdFundingVO;
import com.app.onestepback.domain.model.FundingPaymentVO;
import com.app.onestepback.domain.model.PaymentMethodVO;
import com.app.onestepback.domain.type.crowdfunding.CrowdFundingStatus;
import com.app.onestepback.domain.type.notification.NotificationType;
import com.app.onestepback.global.client.PortOneClient;
import com.app.onestepback.global.common.Slice;
import com.app.onestepback.global.exception.BusinessException;
import com.app.onestepback.global.exception.ErrorCode;
import com.app.onestepback.repository.CrowdFundingMapper;
import com.app.onestepback.repository.FundingPaymentMapper;
import com.app.onestepback.repository.PaymentHistoryMapper;
import com.app.onestepback.repository.PaymentMethodMapper;
import com.app.onestepback.service.file.FileService;
import com.app.onestepback.service.notification.event.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.app.onestepback.domain.type.crowdfunding.CrowdFundingStatus.*;

/**
 * 크라우드 펀딩 비즈니스 로직을 처리하는 핵심 서비스 구현체입니다.
 * <p>
 * 트랜잭션 경계를 메서드 단위로 세밀하게 분리하여 동시성 제어 및 데이터 정합성을 보장합니다.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrowdFundingServiceImpl implements CrowdFundingService {

    private final FileService fileService;
    private final PortOneClient portOneClient;
    private final NotificationPublisher notificationPublisher;

    private final CrowdFundingMapper crowdFundingMapper;
    private final FundingPaymentMapper fundingPaymentMapper;
    private final PaymentHistoryMapper paymentHistoryMapper;
    private final PaymentMethodMapper paymentMethodMapper;

    @Override
    @Transactional
    public Long create(CrowdFundingServiceCommand.Create cmd, MultipartFile mainImg) {
        CrowdFundingStatus status = validateAndGetInitialStatus(cmd);

        CrowdFundingVO vo = CrowdFundingVO.builder()
                .artistId(cmd.artistId())
                .writerId(cmd.writerId())
                .title(cmd.title())
                .content(cmd.content())
                .targetAmount(cmd.targetAmount())
                .currentAmount(0L)
                .startDate(cmd.startDate())
                .endDate(cmd.endDate())
                .status(status)
                .mainImgId(cmd.fileId())
                .build();
        crowdFundingMapper.insert(vo);

        if (cmd.fileId() != null) {
            fileService.connectFiles(vo.getId(), "CROWDFUNDING_IMAGE", List.of(cmd.fileId()), cmd.writerId());
        }

        /* 일반 회원이 펀딩을 제안한 경우 대상 아티스트에게 즉각적인 알림을 발송합니다. */
        if (!cmd.isWriterArtist()) {
            String url = "/artist/" + cmd.artistId() + "/funding/" + vo.getId();
            String message = "새로운 펀딩 제안이 도착했습니다: " + cmd.title();
            notificationPublisher.send(cmd.writerId(), cmd.artistId(), NotificationType.CROWD_FUNDING_REQUEST, url, message);
        }

        return vo.getId();
    }

    @Override
    @Transactional
    public Long fund(Long fundingId, Long memberId, Long paymentMethodId, Long amount) {
        CrowdFundingDTO.Detail funding = crowdFundingMapper.selectById(fundingId).orElseThrow(
                () -> new BusinessException(ErrorCode.CROWD_FUNDING_NOT_FOUND)
        );

        if (funding.status() != PROCEEDING) {
            throw new BusinessException(ErrorCode.CROWD_FUNDING_NOT_PROCEEDING);
        }

        PaymentMethodVO paymentMethod = paymentMethodMapper.selectById(paymentMethodId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_METHOD_NOT_FOUND));

        if (!paymentMethod.getMemberId().equals(memberId)) {
            log.warn("[Security Alert] 비정상적인 타인 결제 수단 접근 감지 - MemberID: {}, PaymentMethodID: {}", memberId, paymentMethodId);
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        String merchantUid = "fund_" + UUID.randomUUID();
        PaymentResultDTO paymentResult;

        try {
            paymentResult = portOneClient.payWithBillingKey(
                    paymentMethod.getBillingKey(),
                    merchantUid,
                    amount,
                    "[펀딩] " + funding.title()
            );
        } catch (Exception e) {
            log.error("펀딩 외부 결제 승인 요청 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.PAYMENT_FAILED);
        }

        try {
            FundingPaymentVO paymentVO = FundingPaymentVO.builder()
                    .fundingId(fundingId)
                    .memberId(memberId)
                    .merchantUid(merchantUid)
                    .impUid(paymentResult.getImpUid())
                    .amount(amount)
                    .status("PAID")
                    .build();

            fundingPaymentMapper.insert(paymentVO);

            long oldAmount = funding.currentAmount();
            long newCurrentAmount = oldAmount + amount;
            long targetAmount = funding.targetAmount();

            CrowdFundingVO updateVO = CrowdFundingVO.builder()
                    .id(fundingId)
                    .currentAmount(newCurrentAmount)
                    .build();
            crowdFundingMapper.update(updateVO);

            log.info("펀딩 후원 처리 완료 - FundingID: {}, MemberID: {}, Amount: {}", fundingId, memberId, amount);

            /* 해당 결제로 인해 목표 금액을 최초로 달성한 경우 대상 아티스트에게 축하 알림을 발송합니다. */
            if (oldAmount < targetAmount && newCurrentAmount >= targetAmount) {
                String url = "/artist/" + funding.artistId() + "/funding/" + fundingId;
                String message = "축하합니다! '" + funding.title() + "' 프로젝트가 목표 금액 100%를 달성했습니다.";
                notificationPublisher.send(null, funding.artistId(), NotificationType.CROWD_FUNDING_GOAL_REACHED, url, message);
            }

            return paymentVO.getId();
        } catch (Exception e) {
            log.error("내부 시스템 저장 실패로 인한 승인 결제 자동 취소 연동 시작. impUid: {}", paymentResult.getImpUid(), e);
            try {
                portOneClient.cancelPayment(paymentResult.getImpUid(), "시스템 오류로 인한 결제 자동 취소", amount);
            } catch (Exception cancelError) {
                log.error("자동 결제 취소 실패 (관리자 수동 환불 처리 요망). impUid: {}", paymentResult.getImpUid());
            }
            throw new BusinessException(ErrorCode.PAYMENT_SYSTEM_ERROR);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CrowdFundingDTO.ListView> getList(Long artistId) {
        return crowdFundingMapper.selectListByArtistId(artistId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CrowdFundingDTO.PublicList> getFundingList(String status) {
        if (status == null || status.isBlank()) {
            status = "ALL";
        }
        return crowdFundingMapper.selectFundingList(status);
    }

    @Override
    @Transactional(readOnly = true)
    public CrowdFundingDTO.Detail getDetail(Long fundingId) {
        return crowdFundingMapper.selectById(fundingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CROWD_FUNDING_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public CrowdFundingDTO.PaymentView getPaymentInfo(Long fundingId, Long memberId) {
        return crowdFundingMapper.selectPaymentView(fundingId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CROWD_FUNDING_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public CrowdFundingDTO.Receipt getPaymentReceipt(Long fundingId, Long paymentId, Long memberId) {
        return fundingPaymentMapper.selectReceipt(paymentId, fundingId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<FundingPaymentDTO.History> getMyFundingHistory(Long memberId, Long lastPaymentId, int size) {
        /* 데이터베이스 조회 시 size + 1건을 요청하여 다음 페이지 존재 여부를 확인합니다.
           이후 Slice 객체가 초과된 데이터를 자동으로 절삭하여 반환합니다. */
        List<FundingPaymentDTO.History> content = fundingPaymentMapper.selectFundingPaymentHistory(memberId, lastPaymentId, size);
        return new Slice<>(content, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CrowdFundingVO> getExpiredEndedFundings() {
        return crowdFundingMapper.selectExpiredEndedFundings();
    }

    @Override
    @Transactional
    public void editAndAccept(CrowdFundingServiceCommand.EditAccept cmd) {
        CrowdFundingDTO.Detail funding = getFundingAndValidateOwner(cmd.fundingId(), cmd.artistId());

        if (funding.status() != WAITING) {
            throw new BusinessException(ErrorCode.CROWD_FUNDING_NOT_WAITING);
        }
        if (!cmd.endDate().isAfter(cmd.startDate())) {
            throw new BusinessException(ErrorCode.CROWD_FUNDING_INVALID_DATES);
        }

        CrowdFundingVO updateVO = CrowdFundingVO.builder()
                .id(cmd.fundingId())
                .title(cmd.title())
                .content(cmd.content())
                .targetAmount(cmd.targetAmount())
                .startDate(cmd.startDate())
                .endDate(cmd.endDate())
                .mainImgId(cmd.fileId())
                .status(PROCEEDING)
                .build();
        crowdFundingMapper.update(updateVO);

        if (cmd.fileId() != null) {
            fileService.connectFiles(cmd.fundingId(), "CROWDFUNDING_IMAGE", List.of(cmd.fileId()), cmd.artistId());
        }

        /* 제안자에게 펀딩 프로젝트 승낙 및 오픈 알림을 발송합니다. */
        if (!Objects.equals(funding.writerId(), cmd.artistId())) {
            String url = "/artist/" + cmd.artistId() + "/funding/" + cmd.fundingId();
            String message = "'" + funding.title() + "' 프로젝트 제안이 승낙되어 정상적으로 오픈되었습니다.";
            notificationPublisher.send(cmd.artistId(), funding.writerId(), NotificationType.CROWD_FUNDING_ACCEPTED, url, message);
        }
    }

    @Override
    @Transactional
    public void reject(Long fundingId, Long artistId, String reason) {
        CrowdFundingDTO.Detail funding = getFundingAndValidateOwner(fundingId, artistId);

        if (funding.status() != WAITING) {
            throw new BusinessException(ErrorCode.CROWD_FUNDING_NOT_WAITING);
        }

        CrowdFundingVO updateVO = CrowdFundingVO.builder()
                .id(fundingId)
                .status(REJECTED)
                .rejectReason(reason)
                .build();
        crowdFundingMapper.update(updateVO);

        /* 제안자에게 펀딩 프로젝트 반려 알림을 발송합니다. */
        if (!Objects.equals(funding.writerId(), artistId)) {
            String url = "/artist/" + artistId + "/funding/" + fundingId;
            String message = "'" + funding.title() + "' 프로젝트 제안이 아쉽게도 반려되었습니다.";
            notificationPublisher.send(artistId, funding.writerId(), NotificationType.CROWD_FUNDING_REJECTED, url, message);
        }
    }

    @Override
    @Transactional
    public void success(Long fundingId, Long artistId) {
        CrowdFundingDTO.Detail funding = getFundingAndValidateOwner(fundingId, artistId);

        if (funding.status() != ENDED) {
            throw new BusinessException(ErrorCode.CROWD_FUNDING_NOT_ENDED);
        }

        changeFundingStatus(fundingId, SUCCESS);

        int transferredCount = paymentHistoryMapper.insertFundingIncomeBulk(fundingId, artistId);

        List<FundingPaymentVO> payments = fundingPaymentMapper.selectPaidPaymentsByFundingId(fundingId);
        List<Long> participantIds = payments.stream()
                .map(FundingPaymentVO::getMemberId)
                .distinct()
                .toList();

        /* 프로젝트 참여자 전원에게 펀딩 최종 성공 알림을 일괄 발송합니다. */
        if (!participantIds.isEmpty()) {
            String url = "/artist/" + artistId + "/funding/" + fundingId;
            notificationPublisher.sendBroadcast(artistId, participantIds, NotificationType.CROWD_FUNDING_SUCCESS, url);
        }

        log.info("펀딩 프로젝트 성공 처리 완료. 자산 이관 건수: {}건, 알림 대상자: {}명, FundingID: {}", transferredCount, participantIds.size(), fundingId);
    }

    @Override
    @Transactional
    public void fail(Long fundingId, Long artistId, String reason) {
        CrowdFundingDTO.Detail funding = getFundingAndValidateOwner(fundingId, artistId);

        if (funding.status() != ENDED) {
            throw new BusinessException(ErrorCode.CROWD_FUNDING_NOT_ENDED);
        }

        List<FundingPaymentVO> payments = fundingPaymentMapper.selectPaidPaymentsByFundingId(fundingId);

        /* 결제 취소(환불) 처리를 수행하고 정상적으로 환불된 회원의 식별자 목록을 반환받습니다. */
        List<Long> refundedMemberIds = processRefunds(payments, reason);

        if (!payments.isEmpty()) {
            fundingPaymentMapper.updatePaymentsToRefunded(fundingId);
        }

        CrowdFundingVO updateVO = CrowdFundingVO.builder()
                .id(fundingId)
                .status(FAILED)
                .rejectReason(reason)
                .build();
        crowdFundingMapper.update(updateVO);

        List<Long> targetParticipantIds = refundedMemberIds.stream()
                .distinct()
                .toList();

        /* 환불 처리에 성공한 유효 참여자에게만 실패 및 환불 안내 알림을 발송합니다. */
        if (!targetParticipantIds.isEmpty()) {
            String url = "/artist/" + artistId + "/funding/" + fundingId;
            notificationPublisher.sendBroadcast(artistId, targetParticipantIds, NotificationType.CROWD_FUNDING_FAILED, url);
        }

        log.info("펀딩 프로젝트 실패 및 일괄 환불 완료. FundingID: {}, 환불/알림 발송 건수: {}건", fundingId, targetParticipantIds.size());
    }

    @Override
    @Transactional
    public int updateExpiredFundings() {
        return crowdFundingMapper.updateExpiredFundings();
    }


    // ===================================================================================
    // Private Helper Methods
    // ===================================================================================

    /**
     * 펀딩 프로젝트 생성 시 작성자의 권한(아티스트 본인 여부)에 따라 초기 상태를 결정합니다.
     */
    private static CrowdFundingStatus validateAndGetInitialStatus(CrowdFundingServiceCommand.Create cmd) {
        boolean isArtist = cmd.isWriterArtist();

        if (isArtist) {
            if (cmd.startDate() == null || cmd.endDate() == null) {
                throw new BusinessException(ErrorCode.CROWD_FUNDING_DATES_REQUIRED);
            }
            if (!cmd.endDate().isAfter(cmd.startDate())) {
                throw new BusinessException(ErrorCode.CROWD_FUNDING_INVALID_DATES);
            }
        }

        return isArtist ? PROCEEDING : WAITING;
    }

    /**
     * 펀딩 상세 정보를 조회하고 요청한 아티스트가 해당 펀딩의 소유자인지 권한을 검증합니다.
     */
    private CrowdFundingDTO.Detail getFundingAndValidateOwner(Long fundingId, Long artistId) {
        CrowdFundingDTO.Detail funding = crowdFundingMapper.selectById(fundingId).orElseThrow(
                () -> new BusinessException(ErrorCode.CROWD_FUNDING_NOT_FOUND)
        );

        if (!Objects.equals(funding.artistId(), artistId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        return funding;
    }

    /**
     * 특정 펀딩 프로젝트의 상태만을 단일 갱신합니다.
     */
    private void changeFundingStatus(Long fundingId, CrowdFundingStatus status) {
        CrowdFundingVO updateVO = CrowdFundingVO.builder()
                .id(fundingId)
                .status(status)
                .build();
        crowdFundingMapper.update(updateVO);
    }

    /**
     * 포트원 결제망을 통해 승인된 결제 건들을 일괄 취소(환불) 처리하고, 성공한 회원의 ID 목록을 반환합니다.
     */
    private List<Long> processRefunds(List<FundingPaymentVO> payments, String reason) {
        List<Long> refundedMemberIds = new ArrayList<>();

        for (FundingPaymentVO payment : payments) {
            if (payment.getImpUid() != null) {
                try {
                    portOneClient.cancelPayment(payment.getImpUid(), reason, payment.getAmount());
                    refundedMemberIds.add(payment.getMemberId());
                } catch (Exception e) {
                    log.error("외부 연동 결제 취소 실패 (관리자 수동 처리 요망). impUid: {}, error: {}", payment.getImpUid(), e.getMessage());
                    throw new BusinessException(ErrorCode.PAYMENT_SYSTEM_ERROR);
                }
            }
        }
        return refundedMemberIds;
    }
}