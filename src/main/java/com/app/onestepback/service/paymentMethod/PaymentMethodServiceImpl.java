package com.app.onestepback.service.paymentMethod;

import com.app.onestepback.domain.dto.payment.PaymentMethodDTO;
import com.app.onestepback.domain.dto.portone.PortOneBillingKeyResponse;
import com.app.onestepback.domain.model.PaymentMethodVO;
import com.app.onestepback.global.client.PortOneClient;
import com.app.onestepback.global.exception.BusinessException;
import com.app.onestepback.global.exception.ErrorCode;
import com.app.onestepback.global.util.PaymentValidator;
import com.app.onestepback.repository.PaymentMethodMapper;
import com.app.onestepback.repository.SubscriptionMapper;
import com.app.onestepback.service.paymentMethod.cmd.PaymentRegisterCmd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 결제 수단 관리를 위한 비즈니스 로직 구현체입니다.
 * <p>
 * 외부 결제망(PG)과의 빌링키 동기화 및 기본 결제 수단 변경에 따른 데이터 무결성을 엄격하게 보장합니다.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentValidator paymentValidator;
    private final PortOneClient portOneClient;

    private final PaymentMethodMapper paymentMethodMapper;
    private final SubscriptionMapper subscriptionMapper;

    @Override
    @Transactional
    public PaymentMethodDTO register(PaymentRegisterCmd cmd) {
        PortOneBillingKeyResponse res = paymentValidator.validateRegistration(cmd.billingKey());

        String fullCardNumber = res.getResponse().getCardNumber();
        String last4Digit = fullCardNumber.substring(fullCardNumber.length() - 4);

        PaymentMethodVO vo = PaymentMethodVO.builder()
                .memberId(cmd.memberId())
                .billingKey(cmd.billingKey())
                .last4Digit(last4Digit)
                .cardName(cmd.cardName() != null ? cmd.cardName() : "등록된 카드")
                .isDefault(cmd.setDefault())
                .build();
        paymentMethodMapper.insert(vo);

        return PaymentMethodDTO.from(vo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentMethodDTO> getList(Long memberId) {
        return paymentMethodMapper.selectAllByMemberId(memberId);
    }

    @Override
    @Transactional
    public void updateDefault(Long memberId, Long paymentId) {
        paymentMethodMapper.updateAllDefaultToN(memberId);

        int updated = paymentMethodMapper.updateDefaultToY(paymentId, memberId);

        if (updated == 0) {
            throw new BusinessException(ErrorCode.PAYMENT_METHOD_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public void delete(Long memberId, Long paymentId) {
        PaymentMethodVO card = paymentMethodMapper.selectById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_METHOD_NOT_FOUND));

        if (!card.getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        if (card.isDefault()) {
            throw new BusinessException(ErrorCode.DEFAULT_PAYMENT_DELETE_NOT_ALLOWED);
        }

        subscriptionMapper.updatePaymentMethodToNull(paymentId);

        portOneClient.deleteBillingKey(card.getBillingKey());

        paymentMethodMapper.deleteById(paymentId);
    }
}