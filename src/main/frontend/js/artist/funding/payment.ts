/**
 * @file payment.ts
 * @package artist/funding
 * @description 크라우드 펀딩 후원 시 결제 수단을 로드하고, PortOne API를 통한 결제 처리를 수행하는 모듈입니다.
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

declare global {
    interface Window {
        IMP: any;
    }
}

$(() => {
    // ===================================================================================
    //  SECTION: Initialization & Extraction
    // ===================================================================================

    const fundingId = $('#fundingId').val() as string;
    const amount = parseInt($('#amount').val() as string, 10);
    const fundingTitle = $('#fundingTitle').val() as string;

    const $wrapper = $('.subscription-wrapper');
    const portoneMerchantId = $wrapper.data('portone-id') as string;
    const memberId = $wrapper.data('member-id') as number;
    const hasPaymentMethod = $wrapper.data('has-payment') === true;

    const IMP = window.IMP;
    if (portoneMerchantId && IMP) {
        IMP.init(portoneMerchantId);
    }

    let cachedPayments: PaymentMethod[] = [];

    // ===================================================================================
    //  SECTION: Render & Helper Functions (Hoisting 방지)
    // ===================================================================================

    const renderActionButton = (): void => {
        const html = `
            <button type="button" class="btn-confirm" id="btnConfirmPayment">
                ${amount.toLocaleString()}원 결제하기
            </button>
            <a href="javascript:history.back()" class="btn-cancel">취소</a>
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

        const html = `
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
        $('#payment-method-wrapper').html(html);

        // 결제 수단이 렌더링되었으므로 결제 버튼을 표시합니다.
        renderActionButton();
    };

    const renderEmptyState = (): void => {
        const html = `
            <div class="empty-payment-box">
                <div class="empty-icon"><i class="far fa-credit-card"></i></div>
                <p>등록된 결제 수단이 없습니다.<br>카드를 등록하고 바로 후원하세요.</p>
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

    const processFundingPayment = (paymentMethodId: number): void => {
        const $btn = $('#btnConfirmPayment');
        $btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i> 결제 진행 중...');

        $.ajax({
            url: `/api/v1/crowd-funding/fund/${fundingId}`,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                amount: amount,
                paymentMethodId: paymentMethodId
            }),
            success: (res: ApiResponse<string>) => {
                Show.success("후원이 성공적으로 완료되었습니다!");

                setTimeout(() => {
                    window.location.href = `success?payment-id=${res.data}`;
                }, 1500);
            },
            error: (xhr: JQuery.jqXHR) => {
                const errRes = xhr.responseJSON as ApiResponse;
                Show.error(errRes?.error?.message || "결제에 실패했습니다.");
                $btn.prop('disabled', false).text(`${amount.toLocaleString()}원 결제하기`);
            }
        });
    };

    // ===================================================================================
    //  SECTION: Event Handlers
    // ===================================================================================

    /**
     * @description 신규 카드 등록 절차를 위해 PortOne 모듈을 호출합니다.
     */
    $(document).on('click', '#btnRegisterNewCard', () => {
        const uuid = crypto.randomUUID();
        const merchantUid = `reg_${uuid}`;
        const customerUid = `member_${memberId}_${uuid}`;

        IMP.request_pay({
            pg: "tosspayments",
            pay_method: "card",
            merchant_uid: merchantUid,
            customer_uid: customerUid,
            name: "결제 수단 등록 (펀딩용)",
            amount: 0
        }, (rsp: any) => {
            if (rsp.success) {
                $.ajax({
                    url: '/api/v1/payments/method',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        billingKey: rsp.customer_uid,
                        cardName: rsp.card_name
                    }),
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
                let msg = "카드 등록 취소";
                if (rsp.error_msg) msg = rsp.error_msg;
                Show.error(msg);
            }
        });
    });

    $(document).on('click', '#currentPaymentBox', function (e: JQuery.ClickEvent) {
        if ($(e.currentTarget).find('.change-btn').length > 0) {
            $('#paymentDropdown').slideToggle(200);
            $(e.currentTarget).find('.change-btn i').toggleClass('fa-chevron-down fa-chevron-up');
        }
    });

    $(document).on('click', '.payment-option', function (e: JQuery.ClickEvent) {
        const $target = $(e.currentTarget);
        const id = $target.data('id') as number;
        const name = $target.data('name') as string;
        const last4 = $target.data('last4') as string;
        const isDefault = $target.data('default') as boolean;

        const defaultTxt = isDefault ? ' (기본)' : '';
        $('#selectedCardName').text(name + defaultTxt);
        $('#selectedCardNum').text('**** **** **** ' + last4);
        $('#selectedPaymentId').val(id);

        $('#paymentDropdown').slideUp(200);
        $('#currentPaymentBox').find('.change-btn i').removeClass('fa-chevron-up').addClass('fa-chevron-down');
    });

    /**
     * @description 결제 전 Confirm 모달을 띄우고 승인 시 펀딩 결제 트랜잭션을 실행합니다.
     */
    $(document).on('click', '#btnConfirmPayment', () => {
        const paymentIdVal = $('#selectedPaymentId').val() as string;

        if (!paymentIdVal) {
            Show.error("결제 수단을 선택해주세요.");
            return;
        }

        Confirm.open({
            title: "펀딩 후원",
            desc: `"${fundingTitle}" 프로젝트에\n${amount.toLocaleString()}원을 후원하시겠습니까?`,
            actionText: "결제하기",
            onConfirm: () => {
                processFundingPayment(parseInt(paymentIdVal, 10));
            }
        });
    });

    // ===================================================================================
    //  SECTION: Bootstrap
    // ===================================================================================

    if (!hasPaymentMethod) {
        renderEmptyState();
    } else {
        loadPaymentMethods();
    }
});