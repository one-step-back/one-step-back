/**
 * @file my-profile.ts
 * @package my-page
 * @description 마이페이지의 사용자 프로필 정보 수정, 비밀번호 변경 및 회원 탈퇴 기능을 제어하는 모듈입니다.
 */

import $ from 'jquery';
import { Show } from '@/util/toast';
import { ApiResponse } from '@/types/api';
import { Confirm } from "@/util/confirm";

$(() => {
    /** @section DOM Elements - 폼 및 입력 요소 참조 */
    const $profileImage = $('#profileImage');
    const $profilePreview = $('#profilePreview');
    const $profileForm = $('#profileForm');
    const $btnUpdateProfile = $('#btnUpdateProfile');

    const $passwordForm = $('#passwordForm');
    const $btnUpdatePassword = $('#btnUpdatePassword');
    const $btnWithdraw = $('#btnWithdraw');

    // ===================================================================================
    //  SECTION: Event Handlers
    // ===================================================================================

    /**
     * @description 프로필 이미지 업로드 시 클라이언트 측 미리보기(Preview)를 렌더링합니다.
     */
    $profileImage.on('change', (e: JQuery.ChangeEvent) => {
        const target = e.target as HTMLInputElement;
        const file = target.files?.[0];

        if (file) {
            const reader = new FileReader();
            reader.onload = (event: ProgressEvent<FileReader>) => {
                const result = event.target?.result as string;
                $profilePreview.css('background-image', `url(${result})`);
            };
            reader.readAsDataURL(file);
        }
    });

    /**
     * @description 사용자 프로필 정보 및 이미지를 서버로 전송하여 업데이트합니다.
     */
    $btnUpdateProfile.on('click', () => {
        const formElement = $profileForm[0] as HTMLFormElement;
        const formData = new FormData(formElement);
        $('.field-error-border').removeClass('field-error-border');

        $.ajax({
            url: '/api/v1/members/update',
            type: 'PATCH',
            data: formData,
            processData: false,
            contentType: false,
            success: () => {
                Show.success("프로필이 성공적으로 수정되었습니다.");
                setTimeout(() => location.reload(), 1000);
            },
            error: (xhr: JQuery.jqXHR) => {
                handleValidationError(xhr, "프로필 수정에 실패했습니다.");
            }
        });
    });

    /**
     * @description 계정의 비밀번호를 변경하는 API를 호출하고 성공 시 로그인 페이지로 유도합니다.
     */
    $btnUpdatePassword.on('click', () => {
        /**
         * @note 파일 데이터가 포함되지 않은 텍스트 폼이므로 JSON 객체로 직렬화하여 서버에 전송합니다.
         */
        const payload = {
            currentPassword: $('#currentPassword').val(),
            newPassword: $('#newPassword').val()
        };

        $('.field-error-border').removeClass('field-error-border');
        $('.field-error').hide().text('');

        $.ajax({
            url: '/api/v1/members/password',
            type: 'PATCH',
            data: JSON.stringify(payload),
            processData: false,
            contentType: 'application/json',
            success: () => {
                Confirm.open({
                    title: "비밀번호 변경 완료",
                    desc: "비밀번호가 변경되었습니다. 다시 로그인해주세요.",
                    actionText: "로그인 이동",
                    onConfirm: () => {
                        location.href = '/member/login';
                    }
                });
            },
            error: (xhr: JQuery.jqXHR) => {
                handleValidationError(xhr, "비밀번호 변경에 실패했습니다.");
            }
        });
    });

    /**
     * @description 회원 탈퇴 로직을 수행하며, 치명적인 작업이므로 확인 모달을 거쳐 재확인합니다.
     */
    $btnWithdraw.on('click', () => {
        Confirm.open({
            title: "회원 탈퇴",
            desc: "정말로 탈퇴하시겠습니까? 탈퇴 시 모든 데이터가 즉시 삭제되며 복구가 불가능합니다.",
            actionText: "탈퇴하기",
            onConfirm: () => {
                $.ajax({
                    url: '/api/v1/members/withdraw',
                    type: 'DELETE',
                    success: () => {
                        alert("탈퇴 처리되었습니다. 그동안 이용해주셔서 감사합니다.");
                        location.href = "/";
                    },
                    error: () => {
                        alert("탈퇴 처리 중 오류가 발생했습니다.");
                    }
                });
            }
        });
    });

    // ===================================================================================
    //  SECTION: Helper Functions
    // ===================================================================================

    /**
     * @function handleValidationError
     * @description 서버 API 응답의 에러 코드를 분석하여 각 필드에 적절한 에러 메시지 및 스타일을 매핑합니다.
     * @param {JQuery.jqXHR} xhr - jQuery AJAX 오류 객체
     * @param {string} defaultMsg - 구체적인 에러 메시지가 없을 경우 출력할 기본 메시지
     */
    function handleValidationError(xhr: JQuery.jqXHR, defaultMsg: string): void {
        let errorMsg = defaultMsg;
        const res = xhr.responseJSON as ApiResponse;

        // [CASE 1] 유효성 검사 실패 (VAL-001)
        if (res?.error?.code === 'VAL-001') {
            const pattern = /\[(.*?): (.*?)]/g;
            let match;
            let isFirst = true;

            while ((match = pattern.exec(res.error.message)) !== null) {
                const fieldName = match[1];
                const msg = match[2];

                if (fieldName === 'profileImage') {
                    $profilePreview.addClass('field-error-border');
                } else {
                    $(`#${fieldName}`).addClass('field-error-border');
                }

                $(`#error-${fieldName}`).text(msg).show();

                // 첫 번째 에러 발생 필드에 포커스를 이동시킵니다.
                if (isFirst) {
                    errorMsg = msg;
                    if (fieldName !== 'profileImage') {
                        // TS6387 경고 방지를 위해 trigger 메서드를 사용합니다.
                        $(`#${fieldName}`).trigger('focus');
                    }
                    isFirst = false;
                }
            }
        }
        // [CASE 2] 일반적인 비즈니스 로직 에러
        else if (res?.error?.message) {
            errorMsg = res.error.message;
        }
        // [CASE 3] 구형 API 응답 구조에 대한 폴백 처리
        else if (res && (res as any).message) {
            errorMsg = (res as any).message;
        }

        Show.error(errorMsg);
    }
});