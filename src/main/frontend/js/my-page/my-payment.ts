/**
 * @file my-payment.ts
 * @package my-page
 * @description 마이페이지의 결제 수단(카드) 조회, 등록, 기본값 설정 및 삭제 로직을 제어하는 모듈입니다.
 */

import $ from 'jquery';
import { Show } from '@/util/toast';
import { Confirm } from '@/util/confirm';
import { ApiResponse } from '@/types/api';

/**
 * @interface PaymentMethod
 * @description 서버로부터 수신되는 개별 결제 수단(카드)의 데이터 구조입니다.
 */
interface PaymentMethod {
    id: number;
    cardName: string;
    last4Digit: string;
    default: boolean | number;
}

/**
 * @description 전역으로 로드된 Iamport 객체에 대한 타입 선언입니다.
 */
declare global {
    interface Window {
        IMP: any;
    }
}

$(() => {
    /** @section DOM Elements & State */
    const IMP = window.IMP;
    let cachedPayments: PaymentMethod[] = [];

    // 레이아웃 파일(my-page-layout.html)에서 주입된 식별자들을 추출합니다.
    const PORTONE_MERCHANT_ID = $('.mypage-wrapper').data('portone-id') as string;
    const CURRENT_MEMBER_ID = $('body').data('member-id') as string;

    // ===================================================================================
    //  SECTION: Initialization
    // ===================================================================================

    if (PORTONE_MERCHANT_ID) {
        IMP.init(PORTONE_MERCHANT_ID);
    }

    loadPaymentMethods();

    // ===================================================================================
    //  SECTION: Core Logic
    // ===================================================================================

    /**
     * @function loadPaymentMethods
     * @description 서버에서 사용자의 등록된 결제 수단 목록을 조회합니다.
     */
    function loadPaymentMethods(): void {
        $.ajax({
            url: '/api/v1/payments',
            type: 'GET',
            success: (response: ApiResponse<PaymentMethod[]>) => {
                // [Gigachad Fix] ApiResponse 규격에 맞춰 data 속성을 추출합니다.
                cachedPayments = response.data || [];

                if (cachedPayments.length === 0) {
                    renderEmptyState();
                } else {
                    renderCardList(cachedPayments);
                }
                renderAddButton(cachedPayments.length);
            },
            error: (xhr: JQuery.jqXHR) => {
                console.error("Failed to load payments:", xhr);
                renderEmptyState();
                renderAddButton(0);
            }
        });
    }

    // ===================================================================================
    //  SECTION: Event Handlers
    // ===================================================================================

    /**
     * @description 신규 카드 등록을 위해 아임포트 결제 모듈을 호출합니다.
     */
    $(document).on('click', '#btnRegisterCard', () => {
        Confirm.open({
            title: "결제 수단 등록",
            desc: "새로운 카드를 등록하시겠습니까? 확인 시 보안 인증창으로 연결됩니다.",
            actionText: "등록 진행",
            onConfirm: () => {
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
                                Show.success("결제 수단이 안전하게 등록되었습니다.");
                                if (!cachedPayments) cachedPayments = [];
                                if (response.data) cachedPayments.push(response.data);

                                renderCardList(cachedPayments);
                                renderAddButton(cachedPayments.length);
                            },
                            error: () => Show.error("등록에 실패했습니다. 관리자에게 문의하세요.")
                        });
                    } else {
                        let msg = "인증에 실패했습니다.";
                        if (rsp.error_msg && rsp.error_msg.includes("CANCELED")) msg = "등록을 취소했습니다.";
                        Show.error(msg);
                    }
                });
            }
        });
    });

    /**
     * @description 선택한 카드를 기본 결제 수단으로 설정하며, 낙관적 업데이트 기법을 적용합니다.
     */
    $(document).on('click', '.btn-set-default', function () {
        const paymentId = $(this).data('id') as number;

        Confirm.open({
            title: "기본 카드 설정",
            desc: "이 카드를 기본 결제 수단으로 설정하시겠습니까?",
            actionText: "설정",
            onConfirm: () => {
                const previousState = JSON.parse(JSON.stringify(cachedPayments));

                // UI를 즉각적으로 갱신 (Optimistic Update)
                cachedPayments = cachedPayments.map(card => ({...card, default: (card.id === paymentId)}));
                renderCardList(cachedPayments);

                $.ajax({
                    url: `/api/v1/payments/method/${paymentId}/default`,
                    type: 'PATCH',
                    success: () => Show.success("기본 결제 수단이 변경되었습니다."),
                    error: () => {
                        Show.error("변경에 실패했습니다.");
                        cachedPayments = previousState; // 상태 롤백
                        renderCardList(cachedPayments);
                    }
                });
            }
        });
    });

    /**
     * @description 선택한 카드를 삭제 요청합니다.
     */
    $(document).on('click', '.btn-delete', function () {
        const paymentId = $(this).data('id') as number;

        Confirm.open({
            title: "결제 수단 삭제",
            desc: "정말로 삭제하시겠습니까? 삭제 후에는 복구할 수 없습니다.",
            actionText: "삭제",
            onConfirm: () => {
                const previousState = JSON.parse(JSON.stringify(cachedPayments));

                cachedPayments = cachedPayments.filter(card => card.id !== paymentId);
                (cachedPayments.length === 0) ? renderEmptyState() : renderCardList(cachedPayments);
                renderAddButton(cachedPayments.length);

                $.ajax({
                    url: `/api/v1/payments/method/${paymentId}`,
                    type: 'DELETE',
                    success: () => Show.success("카드가 삭제되었습니다."),
                    error: () => {
                        Show.error("삭제에 실패했습니다.");
                        cachedPayments = previousState; // 상태 롤백
                        (cachedPayments.length === 0) ? renderEmptyState() : renderCardList(cachedPayments);
                        renderAddButton(cachedPayments.length);
                    }
                });
            }
        });
    });

    // ===================================================================================
    //  SECTION: Render Functions
    // ===================================================================================

    /**
     * @function renderCardList
     * @description 서버로부터 전달받은 카드 배열을 HTML로 변환하여 화면에 출력합니다.
     */
    function renderCardList(payments: PaymentMethod[]): void {
        let html = '<div class="card-list">';
        payments.forEach(card => {
            const isDefault = (card.default === true || card.default === 1);
            const defaultBadge = isDefault ? `<span class="badge-default">기본</span>` : '';
            const activeClass = isDefault ? 'active-card' : '';

            const actionButtons = isDefault
                ? ''
                : `<button type="button" class="btn-text btn-set-default" data-id="${card.id}">기본으로 설정</button>`;

            const deleteButton = isDefault
                ? ''
                : `<button type="button" class="btn-text text-danger btn-delete" data-id="${card.id}">삭제</button>`;

            html += `
                <div class="saved-card-item ${activeClass}">
                    <div class="card-info-group">
                        <div class="card-icon"><i class="fas fa-credit-card"></i></div>
                        <div class="card-text">
                            <div class="card-name-row">
                                <span class="card-name">${card.cardName}</span>
                                ${defaultBadge}
                            </div>
                            <span class="card-number">**** **** **** ${card.last4Digit}</span>
                        </div>
                    </div>
                    <div class="card-actions">
                        ${actionButtons}
                        ${deleteButton}
                    </div>
                </div>`;
        });
        html += '</div>';

        $('#payment-list-container').removeClass('empty-state').addClass('payment-list').html(html);
    }

    /**
     * @function renderEmptyState
     * @description 등록된 카드가 없을 경우 출력되는 빈 화면을 렌더링합니다.
     */
    function renderEmptyState(): void {
        const html = `
            <div class="card-status empty">
                <i class="far fa-credit-card" style="font-size: 48px; margin-bottom: 16px; color: #cbd5e1;"></i>
                <p>등록된 결제 수단이 없습니다.</p>
                <p style="font-size: 13px; margin-top:5px;">멤버십 구독을 위해 카드를 등록해주세요.</p>
            </div>
        `;
        $('#payment-list-container').removeClass('payment-list').addClass('empty-state').html(html);
    }

    /**
     * @function renderAddButton
     * @description 현재 등록된 카드 개수에 따라 추가 버튼 또는 제한 안내 문구를 렌더링합니다.
     */
    function renderAddButton(count: number): void {
        let html = '';
        if (count < 3) {
            html = `<button type="button" id="btnRegisterCard" class="btn btn--secondary" style="width: 100%;">
                        <i class="fas fa-plus"></i> 새 카드 등록하기
                    </button>`;
        } else {
            html = `<div class="max-card-alert">
                        <i class="fas fa-info-circle"></i> 결제 수단은 최대 3개까지 등록 가능합니다.
                    </div>`;
        }
        $('#payment-action-container').html(html);
    }
});