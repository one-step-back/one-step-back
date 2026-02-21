/**
 * @file confirm.ts
 * @package artist/membership
 * @description 멤버십 신규 가입, 업그레이드, 다운그레이드 결제 확인 및 처리를 수행하는 모듈입니다.
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
    default?: boolean;
    isDefault?: boolean;
}

/**
 * @interface SubscriptionResponse
 * @description 구독 결제 및 업그레이드 성공 시 반환되는 응답 구조입니다.
 */
interface SubscriptionResponse {
    redirectUrl: string;
}

declare global {
    interface Window {
        IMP: any;
    }
}

$(() => {
    // ===================================================================================
    //  SECTION: Initialization & Extraction
    // ===================================================================================

    const $wrapper = $('.subscription-wrapper');
    const portoneMerchantId = $wrapper.data('portone-id') as string;
    const hasPaymentMethod = $wrapper.data('has-payment') === true;
    const memberId = $('body').data('member-id') as string;

    const mode = $('#subscriptionMode').val() as string;
    const isMembershipActive = $('#isMembershipActive').val() === 'true';
    const finalAmount = parseInt($('#finalAmount').val() as string, 10) || 0;
    const currentSubId = $('#currentSubscriptionId').val() as string;
    const membershipId = $('#targetMembershipId').val() as string;

    const IMP = window.IMP;
    if (portoneMerchantId && IMP) {
        IMP.init(portoneMerchantId);
    }

    let cachedPayments: PaymentMethod[] = [];

    // ===================================================================================
    //  SECTION: Helpers & Renderers (선언부 먼저 배치)
    // ===================================================================================

    const renderActionButton = (hasPayment: boolean): void => {
        if (!hasPayment && mode !== 'DOWNGRADE' && mode !== 'SAME') {
            $('#action-button-wrapper').empty();
            return;
        }

        const disabledAttr = !isMembershipActive ? 'disabled' : '';
        let btnText = "구독 결제하기";
        let btnClass = "btn-confirm";

        if (mode === 'UPGRADE') btnText = "업그레이드 결제하기";
        if (mode === 'DOWNGRADE') {
            btnText = "멤버십 변경 예약하기";
            btnClass = "btn-confirm btn-secondary";
        }
        if (mode === 'SAME') {
            btnText = "현재 이용 중인 멤버십";
            btnClass = "btn-confirm btn-disabled";
        }
        if (!isMembershipActive) btnText = "현재 가입할 수 없는 멤버십입니다";

        const finalDisabled = (mode === 'SAME' || !isMembershipActive) ? 'disabled' : '';

        const html = `
            <p class="confirmation-notice" style="${mode === 'SAME' ? 'display:none;' : ''}">
                <i class="fas fa-exclamation-circle"></i> 결제/변경 내용을 다시 한번 확인해주세요.
            </p>
            <button type="button" class="${btnClass}" id="btnSubscribe" ${disabledAttr} ${finalDisabled}>
                ${btnText}
            </button>
        `;
        $('#action-button-wrapper').html(html);
    };

    const renderDropdown = (payments: PaymentMethod[]): string => {
        let html = '<div class="payment-dropdown" id="paymentDropdown">';
        payments.forEach(card => {
            const isDefault = (card.default === true || card.isDefault === true);
            html += `
                <div class="payment-option"
                     data-id="${card.id}"
                     data-name="${card.cardName}"
                     data-last4="${card.last4Digit}"
                     data-default="${isDefault}">
                    <div class="card-detail">
                        <div class="card-icon"><i class="fas fa-credit-card"></i></div>
                        <div class="card-text-group">
                            <span class="card-name-txt">
                                ${card.cardName}${isDefault ? ' (기본)' : ''}
                            </span>
                            <span class="card-number-txt">
                                **** **** **** ${card.last4Digit}
                            </span>
                        </div>
                    </div>
                </div>
            `;
        });
        html += '</div>';
        return html;
    };

    const renderPaymentSection = (payments: PaymentMethod[]): void => {
        const selected = payments.find(p => p.default === true || p.isDefault === true) || payments[0];
        const hasMultiple = payments.length > 1;

        const dropdownHtml = hasMultiple ? renderDropdown(payments) : '';
        const changeBtnHtml = hasMultiple ?
            `<button type="button" class="change-btn">변경 <i class="fas fa-chevron-down"></i></button>` : '';

        const isDefault = (selected.default === true || selected.isDefault === true);

        const paymentHtml = `
            <span class="section-label">결제 수단 선택</span>
            <div class="selected-card-box" id="currentPaymentBox">
                <div class="card-detail">
                    <div class="card-icon"><i class="fas fa-credit-card"></i></div>
                    <div class="card-text-group">
                        <span class="card-name-txt" id="selectedCardName">
                            ${selected.cardName}${isDefault ? ' (기본)' : ''}
                        </span>
                        <span class="card-number-txt" id="selectedCardNum">
                            **** **** **** ${selected.last4Digit}
                        </span>
                        <input type="hidden" id="selectedPaymentId" value="${selected.id}">
                    </div>
                </div>
                ${changeBtnHtml}
            </div>
            ${dropdownHtml}
        `;
        $('#payment-method-wrapper').html(paymentHtml);
        renderActionButton(true);
    };

    const renderEmptyState = (): void => {
        const html = `
            <div class="empty-payment-box">
                <div class="empty-icon"><i class="far fa-credit-card"></i></div>
                <p>등록된 결제 수단이 없습니다.<br>카드를 등록하고 바로 구독을 시작하세요.</p>
                <button type="button" id="btnRegisterNewCard" class="btn-register-direct">
                    <i class="fas fa-plus"></i> 새 카드 등록하기
                </button>
            </div>
        `;
        $('#payment-method-wrapper').html(html);
        $('#action-button-wrapper').empty();
    };

    const loadPaymentMethods = (): void => {
        $.ajax({
            url: '/api/v1/payments',
            type: 'GET',
            success: (res: ApiResponse<PaymentMethod[]>) => {
                cachedPayments = res.data || [];
                if (cachedPayments.length === 0) {
                    renderEmptyState();
                } else {
                    renderPaymentSection(cachedPayments);
                }
            },
            error: () => {
                renderEmptyState();
            }
        });
    };

    const calculateNextBillingDate = (): void => {
        const today = new Date();
        today.setMonth(today.getMonth() + 1);
        const nextDate = today.getFullYear() + '.' +
            String(today.getMonth() + 1).padStart(2, '0') + '.' +
            String(today.getDate()).padStart(2, '0');
        $('#nextBillingDate').text(nextDate);
    };

    const selectPayment = (id: number, name: string, last4: string, isDefault: boolean): void => {
        const defaultTxt = isDefault ? ' (기본)' : '';
        $('#selectedCardName').text(name + defaultTxt);
        $('#selectedCardNum').text('**** **** **** ' + last4);
        $('#selectedPaymentId').val(id);
        $('#paymentDropdown').slideUp(200);
        $('#currentPaymentBox').find('.change-btn i').removeClass('fa-chevron-up').addClass('fa-chevron-down');
    };

    // ===================================================================================
    //  SECTION: Core Process Functions
    // ===================================================================================

    const processDowngrade = ($btn: JQuery, subId: string, nextMembershipId: string): void => {
        $btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i> 예약 중...');

        $.ajax({
            url: `/api/v1/subscription/${subId}/downgrade`,
            type: 'PATCH',
            contentType: 'application/json',
            data: JSON.stringify({ nextMembershipId: Number(nextMembershipId) }),
            success: (res: ApiResponse<void>) => {
                Show.success("다음 결제일에 등급이 변경됩니다.");
                setTimeout(() => location.href = "/my-page/subscription", 1500);
            },
            error: (xhr: JQuery.jqXHR) => {
                const errRes = xhr.responseJSON as ApiResponse;
                Show.error("변경 예약 실패: " + (errRes?.error?.message || "오류 발생"));
                $btn.prop('disabled', false).text("변경 예약하기");
            }
        });
    };

    const processUpgrade = ($btn: JQuery, subId: string, newMembershipId: string, paymentId: string, amount: number): void => {
        $btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i> 결제 중...');

        $.ajax({
            url: `/api/v1/subscription/${subId}/upgrade`,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                newMembershipId: Number(newMembershipId),
                paymentMethodId: Number(paymentId),
                amount: amount
            }),
            success: (res: ApiResponse<SubscriptionResponse>) => {
                Show.success("멤버십이 업그레이드 되었습니다!");
                setTimeout(() => location.href = res.data?.redirectUrl || "/artist/success", 1000);
            },
            error: (xhr: JQuery.jqXHR) => {
                const errRes = xhr.responseJSON as ApiResponse;
                Show.error("업그레이드 실패: " + (errRes?.error?.message || "결제 오류"));
                $btn.prop('disabled', false).text("업그레이드 결제하기");
            }
        });
    };

    const processSubscription = ($btn: JQuery, targetId: string, paymentId: string): void => {
        $btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i> 결제 진행 중...');

        $.ajax({
            url: '/api/v1/subscription',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                membershipId: Number(targetId),
                paymentMethodId: Number(paymentId)
            }),
            success: (res: ApiResponse<SubscriptionResponse>) => {
                Show.success("구독이 시작되었습니다! 환영합니다.");
                setTimeout(() => location.href = res.data?.redirectUrl || "/", 1000);
            },
            error: (xhr: JQuery.jqXHR) => {
                const errRes = xhr.responseJSON as ApiResponse;
                Show.error("구독 처리 실패: " + (errRes?.error?.message || "결제 오류"));
                $btn.prop('disabled', false).text("구독 결제하기");
            }
        });
    };

    // ===================================================================================
    //  SECTION: Event Handlers
    // ===================================================================================

    $(document).on('click', '#btnRegisterNewCard', () => {
        const uuid = crypto.randomUUID();
        const merchantUid = `reg_${uuid}`;
        const customerUid = `member_${memberId}_${uuid}`;

        IMP.request_pay({
            pg: "tosspayments",
            pay_method: "card",
            merchant_uid: merchantUid,
            customer_uid: customerUid,
            name: "결제 수단 등록 (구독용)",
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
                    success: (res: ApiResponse<PaymentMethod>) => {
                        Show.success("결제 수단이 등록되었습니다.");
                        if (res.data) {
                            cachedPayments = [res.data];
                            renderPaymentSection(cachedPayments);
                        }
                    },
                    error: (xhr: JQuery.jqXHR) => {
                        const errRes = xhr.responseJSON as ApiResponse;
                        Show.error("등록 실패: " + (errRes?.error?.message || "서버 통신 오류"));
                    }
                });
            } else {
                let msg = "카드 등록에 실패했습니다.";
                if (rsp.error_msg && rsp.error_msg.includes("CANCELED")) msg = "등록을 취소했습니다.";
                Show.error(msg);
            }
        });
    });

    $(document).on('click', '#currentPaymentBox', function () {
        if ($(this).find('.change-btn').length > 0) {
            $('#paymentDropdown').slideToggle(200);
            $(this).find('.change-btn i').toggleClass('fa-chevron-down fa-chevron-up');
        }
    });

    $(document).on('click', '.payment-option', function () {
        const id = $(this).data('id') as number;
        const name = $(this).data('name') as string;
        const last4 = $(this).data('last4') as string;
        const isDefault = $(this).data('default') as boolean;
        selectPayment(id, name, last4, isDefault);
    });

    $(document).on('click', '#btnSubscribe', function () {
        const $btn = $(this);

        if (mode === 'SAME') {
            Show.info("이미 이용 중인 멤버십 등급입니다.");
            return;
        }

        if (mode === 'DOWNGRADE') {
            Confirm.open({
                title: "멤버십 변경 예약",
                desc: "다음 결제일부터 등급이 변경됩니다. 현재 혜택은 결제일까지 유지됩니다. 예약하시겠습니까?",
                actionText: "변경 예약",
                onConfirm: () => processDowngrade($btn, currentSubId, membershipId)
            });
            return;
        }

        const paymentId = $('#selectedPaymentId').val() as string;
        if (!paymentId) {
            Show.error("결제 수단을 선택해주세요.");
            return;
        }

        const confirmTitle = (mode === 'UPGRADE') ? "멤버십 업그레이드" : "구독 결제";
        const confirmDesc = (mode === 'UPGRADE')
            ? `차액 ${finalAmount.toLocaleString()}원이 즉시 결제되며, 바로 혜택이 적용됩니다. 진행하시겠습니까?`
            : "매월 정기 결제가 시작됩니다. 구독하시겠습니까?";

        Confirm.open({
            title: confirmTitle,
            desc: confirmDesc,
            actionText: "결제하기",
            onConfirm: () => {
                if (mode === 'UPGRADE') {
                    processUpgrade($btn, currentSubId, membershipId, paymentId, finalAmount);
                } else {
                    processSubscription($btn, membershipId, paymentId);
                }
            }
        });
    });

    // ===================================================================================
    //  SECTION: Bootstrap (모든 선언이 완료된 후 실행)
    // ===================================================================================

    if (mode === 'NEW') {
        calculateNextBillingDate();
    }

    if (mode === 'DOWNGRADE' || mode === 'SAME') {
        renderActionButton(true);
    } else {
        if (!hasPaymentMethod) {
            renderEmptyState();
        } else {
            loadPaymentMethods();
        }
    }
});