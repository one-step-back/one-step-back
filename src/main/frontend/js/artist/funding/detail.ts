/**
 * @file detail.ts
 * @package artist/funding
 * @description 크라우드 펀딩 상세 페이지의 후원 결제 라우팅 및 아티스트 전용 관리(승낙, 반려, 성공, 실패) 기능을 제어하는 모듈입니다.
 */

import $ from 'jquery';
import {Show} from '@/util/toast';
import {Confirm} from '@/util/confirm';
import {ApiResponse} from '@/types/api';

$(() => {
    // ===================================================================================
    //  SECTION: Initialization & Basic Interactions
    // ===================================================================================

    const artistId = $('#artistId').val() as string;
    const fundingId = $('#fundingId').val() as string;

    /**
     * @description 페이지 로드 후 펀딩 달성률 프로그레스 바 애니메이션을 실행합니다.
     */
    const $progressBar = $('.progress-fill');
    const targetWidth = $progressBar.data('width') as number;
    setTimeout(() => {
        $progressBar.css('width', targetWidth + '%');
    }, 300);

    /**
     * @description 현재 페이지 URL을 클립보드에 복사합니다.
     */
    $('.btn-share').on('click', () => {
        const url = window.location.href;
        navigator.clipboard.writeText(url).then(() => {
            Show.success('링크가 복사되었습니다.');
        }).catch(() => {
            Show.error('링크 복사에 실패했습니다.');
        });
    });

    /**
     * @description 후원 금액 입력 시 숫자 이외의 문자를 제거하고 천 단위 콤마를 적용합니다.
     */
    const $amountInput = $('#fundingAmount');
    $amountInput.on('input', (e: JQuery.TriggeredEvent) => {
        const $target = $(e.currentTarget);
        const val = ($target.val() as string || '').replace(/[^0-9]/g, '');

        if (val) {
            $target.val(Number(val).toLocaleString());
        } else {
            $target.val('');
        }
    });

    /**
     * @description 유효성 검사 후 입력된 금액 파라미터와 함께 결제 확인 페이지로 라우팅합니다.
     */
    $('#btn-fund').on('click', () => {
        const rawAmount = ($amountInput.val() as string).replace(/,/g, '');
        const amount = parseInt(rawAmount || '0', 10);

        if (amount < 1000) {
            Show.error("최소 1,000원 이상 후원 가능합니다.");
            $amountInput.trigger('focus');
            return;
        }

        location.href = `/artist/${artistId}/funding/${fundingId}/payment?amount=${amount}`;
    });

    // ===================================================================================
    //  SECTION: Artist Management Logic (Waiting Status)
    // ===================================================================================

    const $rejectModal = $('#reject-modal');
    const $rejectInput = $('#rejectReasonInput');

    $('#btnRejectFunding').on('click', () => {
        $rejectInput.val('');
        $rejectModal.css('display', 'flex').hide().fadeIn(200);
        setTimeout(() => $rejectInput.trigger('focus'), 200);
    });

    $('#reject-cancel-btn').on('click', () => {
        $rejectModal.fadeOut(200);
    });

    $rejectModal.on('click', (e: JQuery.ClickEvent) => {
        // 모달 바깥 영역 클릭 무시 (입력 중 데이터 유실 방지)
        if ($(e.target).is($rejectModal)) {
            // Do nothing
        }
    });

    /**
     * @description 펀딩 제안을 반려 처리하고 사유를 서버에 전송합니다.
     */
    $('#reject-action-btn').on('click', function (e: JQuery.ClickEvent) {
        const reason = ($rejectInput.val() as string).trim();

        if (!reason) {
            Show.error("반려 사유를 입력해주세요.");
            $rejectInput.trigger('focus');
            return;
        }

        const $btn = $(e.currentTarget);
        $btn.prop('disabled', true).text('처리 중...');

        $.ajax({
            url: `/api/v1/crowd-funding/${fundingId}/reject`,
            type: 'PATCH',
            contentType: 'application/json',
            data: JSON.stringify({rejectReason: reason}),
            success: () => {
                Show.success("반려 처리되었습니다.");
                setTimeout(() => location.reload(), 1000);
            },
            error: (xhr: JQuery.jqXHR) => {
                const res = xhr.responseJSON as ApiResponse;
                Show.error(res?.error?.message || "처리 중 오류가 발생했습니다.");
                $btn.prop('disabled', false).text('반려하기');
            }
        });
    });

    /**
     * @description 제안된 펀딩을 수정 후 승낙하기 위해 에디터 페이지로 진입합니다.
     */
    $('#btnAcceptFunding').on('click', () => {
        location.href = `/artist/${artistId}/funding/${fundingId}/edit`;
    });

    // ===================================================================================
    //  SECTION: Artist Management Logic (Ended Status)
    // ===================================================================================

    /**
     * @description 기한이 종료된 펀딩을 성공으로 확정 지어 후원금을 정산 대기 상태로 전환합니다.
     */
    $('#btnSuccessFunding').on('click', () => {
        Confirm.open({
            title: '펀딩 성공 처리',
            desc: '펀딩을 성공으로 처리하시겠습니까? 결제가 확정되며 후원금이 수익으로 이관됩니다. 이 작업은 되돌릴 수 없습니다.',
            actionText: '성공으로 처리',
            onConfirm: () => {
                const $btn = $('#btnSuccessFunding');
                $btn.prop('disabled', true).text('처리 중...');

                $.ajax({
                    url: `/api/v1/crowd-funding/${fundingId}/success`,
                    type: 'PATCH',
                    success: () => {
                        Show.success('성공적으로 펀딩이 마감되었습니다.');
                        setTimeout(() => location.reload(), 1000);
                    },
                    error: (xhr: JQuery.jqXHR) => {
                        const res = xhr.responseJSON as ApiResponse;
                        Show.error(res?.error?.message || "성공 처리에 실패했습니다.");
                        $btn.prop('disabled', false).text('성공 (진행)');
                    }
                });
            }
        });
    });

    const $failModal = $('#fail-modal');
    const $failInput = $('#failReasonInput');

    $('#btnFailFunding').on('click', () => {
        $failInput.val('');
        $failModal.css('display', 'flex').hide().fadeIn(200);
        setTimeout(() => $failInput.trigger('focus'), 200);
    });

    $('#fail-cancel-btn').on('click', () => {
        $failModal.fadeOut(200);
    });

    $failModal.on('click', (e: JQuery.ClickEvent) => {
        if ($(e.target).is($failModal)) {
            // Do nothing
        }
    });

    /**
     * @description 기한이 종료된 펀딩을 실패 처리하고, 참여자들의 결제를 취소/환불합니다.
     */
    $('#fail-action-btn').on('click', function (e: JQuery.ClickEvent) {
        const reason = ($failInput.val() as string).trim();

        if (!reason) {
            Show.error("실패(환불) 사유를 반드시 입력해주세요.");
            $failInput.trigger('focus');
            return;
        }

        const $btn = $(e.currentTarget);
        $btn.prop('disabled', true).text('환불 진행 중...');

        $.ajax({
            url: `/api/v1/crowd-funding/${fundingId}/fail`,
            type: 'PATCH',
            contentType: 'application/json',
            data: JSON.stringify({rejectReason: reason}),
            success: () => {
                Show.success('펀딩이 실패 처리되고 전액 환불되었습니다.');
                setTimeout(() => location.reload(), 1500);
            },
            error: (xhr: JQuery.jqXHR) => {
                const res = xhr.responseJSON as ApiResponse;
                Show.error(res?.error?.message || "처리 중 오류가 발생했습니다.");
                $btn.prop('disabled', false).text('전액 환불하기');
            }
        });
    });
});