package com.app.onestepback.service.paymentMethod;

import com.app.onestepback.domain.dto.payment.PaymentMethodDTO;
import com.app.onestepback.service.paymentMethod.cmd.PaymentRegisterCmd;

import java.util.List;

/**
 * 사용자의 결제 수단(카드, 빌링키 등)을 관리하는 비즈니스 로직 인터페이스입니다.
 */
public interface PaymentMethodService {

    /**
     * 외부 PG사(포트원)의 빌링키를 검증하고 새로운 결제 수단을 시스템에 등록합니다.
     *
     * @param cmd 결제 수단 등록에 필요한 데이터가 담긴 커맨드 객체
     * @return 등록 완료된 결제 수단의 정보 DTO
     */
    PaymentMethodDTO register(PaymentRegisterCmd cmd);

    /**
     * 특정 회원이 등록한 모든 결제 수단 목록을 조회합니다.
     *
     * @param memberId 조회할 회원의 고유 식별자
     * @return 결제 수단 DTO 리스트
     */
    List<PaymentMethodDTO> getList(Long memberId);

    /**
     * 특정 회원의 기본 결제 수단을 지정된 항목으로 변경합니다.
     *
     * @param memberId  요청 회원의 고유 식별자
     * @param paymentId 기본으로 설정할 결제 수단의 고유 식별자
     */
    void updateDefault(Long memberId, Long paymentId);

    /**
     * 등록된 결제 수단을 삭제하고, 외부 PG사(포트원)에 등록된 빌링키 동기화 삭제를 수행합니다.
     * <p>
     * 기본 결제 수단으로 지정된 항목은 안정성을 위해 삭제할 수 없습니다.
     * </p>
     *
     * @param memberId  요청 회원의 고유 식별자
     * @param paymentId 삭제할 결제 수단의 고유 식별자
     */
    void delete(Long memberId, Long paymentId);
}