/**
 * @file my-subscription.ts
 * @package my-page
 * @description 구독 내역 조회, 결제 수단 변경 및 자동 결제 해지 예약 기능을 제어하는 모듈입니다.
 */

import $ from 'jquery';
import { Show } from '@/util/toast';
import { Confirm } from '@/util/confirm';
import { ApiResponse } from '@/types/api';

/**
 * @interface PaymentMethod
 * @description 서버로부터 수신되는 결제 수단 데이터 구조입니다.
 */
interface PaymentMethod {
    id: number;
    cardName: string;
    last4Digit: string;
    default: boolean | number;
}

/**
 * @description 전역 객체 및 동적으로 바인딩되는 함수에 대한 타입 선언입니다.
 */
declare global {
    interface Window {
        IMP: any;
        selectPaymentMethod: (paymentId: number, cardName: string, last4Digit: string) => void;
    }
}

$(() => {
    /** @section Initialization - 식별자 추출 및 외부 모듈 초기화 */
    const IMP = window.IMP;
    const PORTONE_MERCHANT_ID = $('.mypage-wrapper').data('portone-id') as string;
    const CURRENT_MEMBER_ID = $('body').data('member-id') as string;

    if (PORTONE_MERCHANT_ID) {
        IMP.init(PORTONE_MERCHANT_ID);
    }

    let targetSubId: number | null = null;
    let targetSubPaymentId: number | null = null;
    let cachedPayments: PaymentMethod[] = [];

    // ===================================================================================
    //  SECTION: Modal & Payment Logic
    // ===================================================================================

    /**
     * @description 결제 수단 변경 버튼 클릭 시 모달을 활성화하고 데이터를 로드합니다.
     */
    $(document).on('click', '.btn-change-payment', function () {
        const $this = $(this);
        targetSubId = $this.data('id') as number;
        targetSubPaymentId = $this.data('current-payment') as number;
        const artistName = $this.data('artist') as string;

        $('#paymentChangeModal h3').text(`결제 수단 변경 (${artistName})`);
        $('#paymentChangeModal').fadeIn(200);

        loadModalPayments();
    });

    /**
     * @description 모달 닫기 버튼 또는 오버레이 클릭 시 모달을 비활성화합니다.
     */
    $(document).on('click', '.btn-close-modal, .modal-overlay', function (e: JQuery.ClickEvent) {
        if (e.target === this || $(this).hasClass('btn-close-modal') || $(this).parent().hasClass('btn-close-modal')) {
            $('#paymentChangeModal').fadeOut(200);
            targetSubId = null;
            targetSubPaymentId = null;
        }
    });

    /**
     * @function loadModalPayments
     * @description 사용자의 결제 수단 목록을 조회하여 모달 내부에 렌더링합니다.
     */
    function loadModalPayments(): void {
        $.ajax({
            url: '/api/v1/payments',
            type: 'GET',
            success: (response: ApiResponse<PaymentMethod[]>) => {
                cachedPayments = response.data || [];
                renderModalCardList(cachedPayments);
                renderModalAddButton(cachedPayments.length);
            },
            error: (xhr: JQuery.jqXHR) => {
                console.error("Failed to load payments:", xhr);
                $('#modal-payment-list').html('<p class="text-center">결제 수단을 불러오지 못했습니다.</p>');
            }
        });
    }

    /**
     * @function renderModalCardList
     * @description 결제 수단 데이터를 기반으로 모달 리스트 HTML을 생성합니다.
     */
    function renderModalCardList(payments: PaymentMethod[]): void {
        if (!payments || payments.length === 0) {
            $('#modal-payment-list').html(`
                <div class="empty-card-state">
                    <i class="far fa-credit-card"></i>
                    <p>등록된 카드가 없습니다.</p>
                </div>
            `);
            return;
        }

        let html = '';
        payments.forEach(card => {
            const isDefault = (card.default === true || card.default === 1);
            const isCurrent = (card.id === targetSubPaymentId);

            let badges = '';
            if (isDefault) badges += `<span class="badge-mini">기본</span>`;
            if (isCurrent) badges += `<span class="badge-mini current">현재 사용 중</span>`;

            const btnHtml = isCurrent
                ? `<button type="button" class="btn-select disabled" disabled>사용 중</button>`
                : `<button type="button" class="btn-select" onclick="selectPaymentMethod(${card.id}, '${card.cardName}', '${card.last4Digit}')">선택</button>`;

            html += `
                <div class="modal-card-item ${isCurrent ? 'current-item' : ''}">
                    <div class="card-left">
                        <div class="card-icon-box"><i class="fas fa-credit-card"></i></div>
                        <div class="card-texts">
                            <span class="card-name">${card.cardName} ${badges}</span>
                            <span class="card-num">**** **** **** ${card.last4Digit}</span>
                        </div>
                    </div>
                    <div class="card-right">
                        ${btnHtml}
                    </div>
                </div>
            `;
        });
        $('#modal-payment-list').html(html);
    }

    /**
     * @function renderModalAddButton
     * @description 카드 등록 개수에 따라 추가 버튼 또는 안내 메시지를 렌더링합니다.
     */
    function renderModalAddButton(count: number): void {
        let html = '';
        if (count < 3) {
            html = `
                <button type="button" id="btnModalRegisterCard" class="btn-modal-add">
                    <i class="fas fa-plus"></i> 새 카드 등록하기
                </button>`;
        } else {
            html = `<p class="modal-alert-msg"><i class="fas fa-info-circle"></i> 결제 수단은 최대 3개까지 등록 가능합니다.</p>`;
        }
        $('#modal-action-container').html(html);
    }

    /**
     * @description 신규 카드 등록을 위해 아임포트 결제 모듈을 호출합니다.
     */
    $(document).on('click', '#btnModalRegisterCard', () => {
        const uuid = crypto.randomUUID();

        IMP.request_pay({
            pg: "tosspayments",
            pay_method: "card",
            merchant_uid: `reg_${uuid}`,
            customer_uid: `member_${CURRENT_MEMBER_ID}_${uuid}`,
            name: "결제 수단 등록",
            amount: 0
        }, (rsp: any) => {
            if (rsp.success) {
                const payload = {
                    billingKey: rsp.customer_uid,
                    cardName: rsp.card_name
                };

                $.ajax({
                    url: '/api/v1/payments/method',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(payload),
                    success: (response: ApiResponse<PaymentMethod>) => {
                        Show.success("새 카드가 등록되었습니다.");
                        if (response.data) cachedPayments.push(response.data);
                        renderModalCardList(cachedPayments);
                        renderModalAddButton(cachedPayments.length);
                    },
                    error: (xhr: JQuery.jqXHR) => Show.error("등록 실패: " + (xhr.responseText || "오류 발생"))
                });
            } else {
                let msg = "인증 실패";
                if (rsp.error_msg && rsp.error_msg.includes("CANCELED")) msg = "등록 취소";
                Show.error(msg);
            }
        });
    });

    /**
     * @description 선택한 카드로 구독 결제 수단을 변경 요청하고 UI에 즉시 반영합니다.
     */
    window.selectPaymentMethod = function (paymentId: number, cardName: string, last4Digit: string): void {
        if (!targetSubId) return;

        Confirm.open({
            title: "결제 수단 변경",
            desc: `[${cardName}] 카드로 결제 수단을 변경하시겠습니까?`,
            actionText: "변경하기",
            onConfirm: () => {
                $.ajax({
                    url: `/api/v1/subscription/${targetSubId}/payment-method?paymentMethodId=${paymentId}`,
                    type: 'PATCH',
                    success: () => {
                        Show.success("결제 수단이 변경되었습니다.");

                        const $card = $(`#sub-card-${targetSubId}`);
                        const $paymentDisplay = $card.find('.payment-display');
                        const $changeBtn = $card.find('.btn-change-payment');

                        $card.find('.warning-row').remove();
                        $paymentDisplay.find('.payment-warning-text').remove();

                        const newHtml = `
                            <span class="payment-info-text">
                                <i class="far fa-credit-card"></i>
                                <span class="card-name">${cardName}</span>
                                <span class="card-last4">(${last4Digit})</span>
                            </span>
                        `;

                        if ($paymentDisplay.find('.payment-info-text').length > 0) {
                            $paymentDisplay.find('.payment-info-text').replaceWith(newHtml);
                        } else {
                            $paymentDisplay.html(newHtml);
                        }

                        $changeBtn.data('current-payment', paymentId);
                        $('#paymentChangeModal').fadeOut(200);
                    },
                    error: (xhr: JQuery.jqXHR) => {
                        Show.error("변경 실패: " + (xhr.responseText || "오류 발생"));
                    }
                });
            }
        });
    };

    // ===================================================================================
    //  SECTION: Auto Renewal Toggle Logic
    // ===================================================================================

    /**
     * @description 구독 유지 및 취소(자동 갱신 해지) 상태를 토글합니다.
     */
    $(document).on('click', '.btn-toggle-renewal', function () {
        const $btn = $(this);
        const $card = $btn.closest('.subscription-card');
        const $statusRow = $card.find('.renewal-status');

        const subId = $btn.data('id') as number;
        const artistName = $btn.data('artist') as string;
        const currentStatus = $btn.data('status') as boolean;
        const targetStatus = !currentStatus;

        let title: string, desc: string, confirmBtnText: string;
        if (!targetStatus) {
            title = "구독 해지 예약";
            desc = `정말로 [${artistName}] 님의 구독을 해지하시겠습니까?\n해지하더라도 다음 결제일까지 혜택은 유지됩니다.`;
            confirmBtnText = "해지 예약";
        } else {
            title = "구독 유지";
            desc = `[${artistName}] 님의 멤버십 혜택을 다시 유지하시겠습니까?\n다음 결제일에 자동으로 갱신됩니다.`;
            confirmBtnText = "멤버십 유지";
        }

        Confirm.open({
            title: title,
            desc: desc,
            actionText: confirmBtnText,
            onConfirm: () => {
                // Optimistic UI Update
                $btn.data('status', targetStatus);

                if (!targetStatus) {
                    $btn.text("멤버십 유지하기").removeClass('btn-cancel').addClass('btn-resume');
                    $statusRow.slideDown(200);
                    Show.success("해지 예약이 완료되었습니다.");
                } else {
                    $btn.text("구독 취소").removeClass('btn-resume').addClass('btn-cancel');
                    $statusRow.slideUp(200);
                    Show.success("멤버십이 다시 활성화되었습니다.");
                }

                $.ajax({
                    url: `/api/v1/subscription/${subId}/auto-renewal?autoRenewal=${targetStatus}`,
                    type: 'PATCH',
                    error: (xhr: JQuery.jqXHR) => {
                        // Rollback on failure
                        $btn.data('status', currentStatus);
                        if (currentStatus) {
                            $btn.text("구독 취소").removeClass('btn-resume').addClass('btn-cancel');
                            $statusRow.slideUp(200);
                        } else {
                            $btn.text("멤버십 유지하기").removeClass('btn-cancel').addClass('btn-resume');
                            $statusRow.slideDown(200);
                        }
                        Show.error("처리 실패: " + (xhr.responseText || "서버 오류"));
                    }
                });
            }
        });
    });
});