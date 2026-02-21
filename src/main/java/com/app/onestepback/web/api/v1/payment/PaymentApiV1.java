package com.app.onestepback.web.api.v1.payment;

import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.domain.dto.payment.PaymentMethodDTO;
import com.app.onestepback.global.annotation.LoginUser;
import com.app.onestepback.global.common.response.ApiResponse;
import com.app.onestepback.service.paymentMethod.PaymentMethodService;
import com.app.onestepback.service.paymentMethod.cmd.PaymentRegisterCmd;
import com.app.onestepback.web.api.v1.payment.cmd.PaymentMethodRegRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentApiV1 {

    private final PaymentMethodService paymentMethodService;
    private final HttpSession httpSession;

    /**
     * @description 로그인한 사용자가 등록한 모든 결제 수단(카드 등) 목록을 조회합니다.
     * @param user 현재 인가된 사용자의 세션 객체
     * @return 비즈니스 로직 성공 여부와 결제 수단 목록 데이터가 포함된 표준 응답 객체
     */
    @GetMapping
    public ResponseEntity<?> getPaymentMethods(@LoginUser SessionUser user) {
        List<PaymentMethodDTO> payments = paymentMethodService.getList(user.getId());
        return ResponseEntity.ok(ApiResponse.ok(payments));
    }

    /**
     * @description 새로운 결제 수단을 시스템 및 외부 PG사에 등록하고 사용자 세션을 동기화합니다.
     * @param user    현재 인가된 사용자의 세션 객체
     * @param request 빌링키 및 카드 메타데이터가 포함된 요청 객체
     * @return 비즈니스 로직 성공 여부와 등록 완료된 결제 수단 정보가 포함된 표준 응답 객체
     */
    @PostMapping("/method")
    public ResponseEntity<?> register(
            @LoginUser SessionUser user,
            @RequestBody PaymentMethodRegRequest request
    ) {
        boolean setDefault = !user.isHasPaymentMethod();

        PaymentRegisterCmd cmd = new PaymentRegisterCmd(
                user.getId(),
                request.billingKey(),
                request.cardName(),
                setDefault
        );

        PaymentMethodDTO registered = paymentMethodService.register(cmd);

        user.setHasPaymentMethod(true);
        httpSession.setAttribute("member", user);

        log.info("[Payment API] 신규 결제 수단 등록 및 세션 동기화 완료. MemberID: {}", user.getId());
        return ResponseEntity.ok(ApiResponse.ok(registered));
    }

    /**
     * @description 특정 결제 수단을 기본 결제 수단으로 설정합니다.
     * @param user      현재 인가된 사용자의 세션 객체
     * @param paymentId 기본으로 설정할 결제 수단의 고유 식별자
     * @return 비즈니스 로직 성공 여부가 포함된 표준 응답 객체
     */
    @PatchMapping("/method/{paymentId}/default")
    public ResponseEntity<?> updateDefault(
            @LoginUser SessionUser user,
            @PathVariable("paymentId") Long paymentId
    ) {
        paymentMethodService.updateDefault(user.getId(), paymentId);
        log.info("[Payment API] 기본 결제 수단 변경 완료. MemberID: {}, PaymentID: {}", user.getId(), paymentId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    /**
     * @description 등록된 결제 수단을 시스템 및 외부 PG사에서 완전히 삭제합니다.
     * @param user      현재 인가된 사용자의 세션 객체
     * @param paymentId 삭제할 결제 수단의 고유 식별자
     * @return 비즈니스 로직 성공 여부가 포함된 표준 응답 객체
     */
    @DeleteMapping("/method/{paymentId}")
    public ResponseEntity<?> delete(
            @LoginUser SessionUser user,
            @PathVariable("paymentId") Long paymentId
    ) {
        paymentMethodService.delete(user.getId(), paymentId);
        log.info("[Payment API] 결제 수단 삭제 완료. MemberID: {}, PaymentID: {}", user.getId(), paymentId);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}