/**
 * @file account.ts
 * @package artist/setting
 * @description 아티스트 정산 계좌 탭의 정보 조회, 인증 및 저장 기능을 제어합니다.
 */

import $ from 'jquery';
import { Show } from '@/util/toast';
import { Confirm } from '@/util/confirm';
import { ApiResponse } from '@/types/api';

interface AccountData {
    bankCode: string;
    accountNumber: string;
    accountHolder: string;
}

// ===================================================================================
//  Module Level State & DOM Cache
// ===================================================================================
let $formInputs: JQuery;
let $btnVerify: JQuery;
let $btnEdit: JQuery;
let $badge: JQuery;
let $bankSelect: JQuery;
let $accNumber: JQuery;
let $accHolder: JQuery;

// ===================================================================================
//  Core Functions
// ===================================================================================

const fillForm = (data: AccountData): void => {
    $bankSelect.val(data.bankCode);
    $accNumber.val(data.accountNumber);
    $accHolder.val(data.accountHolder);
};

const setFormState = (state: 'LOCKED' | 'EDIT'): void => {
    if (state === 'LOCKED') {
        $formInputs.prop('disabled', true);
        $btnVerify.hide();
        $btnEdit.show();
        $badge.fadeIn();
    } else {
        $formInputs.prop('disabled', false);
        $btnVerify.text('계좌 실명 인증 및 저장').prop('disabled', false).show();
        $btnEdit.hide();
        $badge.hide();
    }
};

export const loadAccountInfo = (): void => {
    $.ajax({
        url: '/api/v1/accounts',
        type: 'GET',
        success: (res: ApiResponse<AccountData>) => {
            const data = res.data;
            if (data && data.accountNumber) {
                fillForm(data);
                setFormState('LOCKED');
            } else {
                setFormState('EDIT');
            }
        },
        error: (xhr: JQuery.jqXHR) => {
            console.error("계좌 정보 로드 실패", xhr);
            setFormState('EDIT');
        }
    });
};

// ===================================================================================
//  Initialization
// ===================================================================================

export function initAccountTab(): void {
    $formInputs = $('#tab-account input, #tab-account select');
    $btnVerify = $('#btn-verify-save');
    $btnEdit = $('#btn-edit-account');
    $badge = $('#account-verified-badge');
    $bankSelect = $('#acc-bank');
    $accNumber = $('#acc-number');
    $accHolder = $('#acc-holder');

    $btnVerify.on('click', () => {
        const bankName = $bankSelect.val() as string;
        const accountNumber = $accNumber.val() as string;
        const accountHolder = $accHolder.val() as string;

        if (!bankName || !accountNumber || !accountHolder) {
            Show.error('은행, 계좌번호, 예금주를 모두 입력해주세요.');
            return;
        }

        const originalBtnText = $btnVerify.text();
        $btnVerify.text('인증 진행 중...').prop('disabled', true);

        const payload: AccountData = {
            bankCode: bankName,
            accountNumber: accountNumber,
            accountHolder: accountHolder
        };

        $.ajax({
            url: '/api/v1/accounts',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(payload),
            success: (res: ApiResponse<string>) => {
                Show.success(res.data || '계좌가 성공적으로 인증 및 저장되었습니다.');
                setFormState('LOCKED');
            },
            error: (xhr: JQuery.jqXHR) => {
                const res = xhr.responseJSON as ApiResponse;
                const msg = res?.error?.message || '오류가 발생했습니다.';
                Show.error('인증 실패: ' + msg);
                $btnVerify.text(originalBtnText).prop('disabled', false);
            }
        });
    });

    $btnEdit.on('click', () => {
        Confirm.open({
            title: "계좌 정보 수정",
            desc: "계좌 정보를 수정하시겠습니까? 수정 시 실명 인증을 다시 진행해야 합니다.",
            actionText: "수정하기",
            onConfirm: () => {
                setFormState('EDIT');
                $bankSelect.trigger('focus');
            }
        });
    });
}