/**
 * @file join.ts
 * @package member
 * @description 회원가입 페이지의 유효성 검사, 이메일 중복 확인, 프로필 이미지 미리보기 및 REST API 전송을 처리하는 모듈입니다.
 */

import $ from 'jquery';
import { Show } from '../util/toast';
import { ApiResponse, ApiErrorResponse } from '../types/api';

/**
 * @interface ValidationStatus
 * @description 폼 제출 전 필수 항목들의 유효성 검사 통과 여부를 저장하는 상태 객체입니다.
 */
interface ValidationStatus {
    email: boolean;
    password: boolean;
    passwordCheck: boolean;
    nickname: boolean;
}

$(() => {
    /** @section DOM Elements - 폼 및 입력 요소 참조 */
    const $joinForm = $('#joinForm');
    const $emailInput = $('#email');
    const $pwInput = $('#rawPassword');
    const $pwCheckInput = $('#passwordCheck');
    const $nicknameInput = $('#nickname');
    const $profileInput = $('#profileImage');
    const $preview = $('#profile-preview');
    const $btnCheckEmail = $('#btn-check-email');

    /**
     * @constant {ValidationStatus} status
     * @description 각 필드의 유효성 검사 통과 상태를 관리합니다.
     */
    const status: ValidationStatus = {
        email: false,
        password: false,
        passwordCheck: false,
        nickname: false
    };

    /**
     * @constant {Record<string, RegExp>} regex
     * @description 각 필드 검증에 사용되는 정규 표현식 모음입니다.
     */
    const regex: Record<string, RegExp> = {
        email: /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/i,
        password: /^(?=.*[A-Za-z])(?=.*\d)(?=.*[$@!%*#?&])[A-Za-z\d$@!%*#?&]{8,20}$/,
        nickname: /^[가-힣a-zA-Z0-9]{2,10}$/
    };

    // ===================================================================================
    //  SECTION: Event Handlers - 프로필 이미지
    // ===================================================================================

    $preview.on('click', () => {
        $profileInput.trigger('click');
    });

    $profileInput.on('change', (e: JQuery.ChangeEvent) => {
        const target = e.target as HTMLInputElement;
        const file = target.files?.[0];

        if (file) {
            const reader = new FileReader();
            reader.onload = (event: ProgressEvent<FileReader>) => {
                const result = event.target?.result;
                $preview.empty().css({
                    'background-image': `url('${result}')`,
                    'background-size': 'cover',
                    'background-position': 'center',
                    'border': 'none'
                });
            };
            reader.readAsDataURL(file);
        } else {
            $preview.empty().append('<i class="fas fa-camera"></i>').css({
                'background-image': 'none',
                'border': '2px dashed #e1e1e1'
            });
        }
    });

    // ===================================================================================
    //  SECTION: Event Handlers - 유효성 검사 및 중복 확인
    // ===================================================================================

    $btnCheckEmail.on('click', () => {
        const email = $emailInput.val() as string;
        const $msgBox = $('#email-msg');

        if (!email) {
            showError($emailInput, $msgBox, "이메일을 입력해주세요.");
            return;
        }
        if (!regex.email.test(email)) {
            showError($emailInput, $msgBox, "올바른 이메일 형식이 아닙니다.");
            return;
        }

        $.ajax({
            url: '/api/v1/members/check-email',
            type: 'GET',
            data: { email: email },
            success: (response: ApiResponse<boolean>) => {
                if (response.success && response.data === true) {
                    showSuccess($emailInput, $msgBox, "사용 가능한 이메일입니다.");
                    status.email = true;
                    $emailInput.prop('readonly', true).addClass('readonly-input');
                    $btnCheckEmail.text('확인완료').prop('disabled', true);
                } else {
                    showError($emailInput, $msgBox, "이미 사용 중인 이메일입니다.");
                    status.email = false;
                }
            },
            error: () => {
                Show.error("서버 통신 중 오류가 발생했습니다.");
                status.email = false;
            }
        });
    });

    $pwInput.on('input', function () {
        const val = $(this).val() as string;
        const $msgBox = $('#rawPassword-msg');

        if (!regex.password.test(val)) {
            showError($(this), $msgBox, "영문, 숫자, 특수문자 포함 8~20자");
            status.password = false;
        } else {
            showSuccess($(this), $msgBox, "사용 가능한 비밀번호입니다.");
            status.password = true;
        }
        $pwCheckInput.trigger('input');
    });

    $pwCheckInput.on('input', function () {
        const val = $(this).val() as string;
        const original = $pwInput.val() as string;
        const $msgBox = $('#password-check-msg');

        if (!val) {
            $msgBox.text('');
            status.passwordCheck = false;
            return;
        }

        if (val !== original) {
            showError($(this), $msgBox, "비밀번호가 일치하지 않습니다.");
            status.passwordCheck = false;
        } else {
            showSuccess($(this), $msgBox, "비밀번호가 일치합니다.");
            status.passwordCheck = true;
        }
    });

    $nicknameInput.on('input', function () {
        const val = $(this).val() as string;
        const $msgBox = $('#nickname-msg');

        if (!regex.nickname.test(val)) {
            showError($(this), $msgBox, "한글, 영문, 숫자 포함 2~10자");
            status.nickname = false;
        } else {
            showSuccess($(this), $msgBox, "멋진 닉네임이네요!");
            status.nickname = true;
        }
    });

    // ===================================================================================
    //  SECTION: Event Handlers - 폼 제출
    // ===================================================================================

    $joinForm.on('submit', (e: JQuery.SubmitEvent) => {
        e.preventDefault();

        if (!checkFinalValidation()) return;

        const formData = new FormData(e.currentTarget as HTMLFormElement);

        $.ajax({
            url: '/api/v1/members/join',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: (response: ApiResponse) => {
                if (response.success) {
                    window.location.href = '/member/login?join=true';
                } else if (response.error) {
                    handleApiError(response.error);
                }
            },
            error: (xhr) => {
                console.error("Join Error:", xhr);
                Show.error("시스템 오류가 발생했습니다. 관리자에게 문의하세요.");
            }
        });
    });

    // ===================================================================================
    //  SECTION: Helper Functions
    // ===================================================================================

    /**
     * @function showError
     * @description 입력 필드에 에러 스타일 및 메시지를 적용합니다.
     * @param {JQuery} $input - 대상 입력 요소
     * @param {JQuery} $msgBox - 메시지를 출력할 요소
     * @param {string} msg - 출력할 에러 메시지
     */
    function showError($input: JQuery, $msgBox: JQuery, msg: string): void {
        $input.addClass('field-error-border').removeClass('field-success-border');
        $msgBox.text(msg).addClass('field-error').removeClass('field-success');
    }

    /**
     * @function showSuccess
     * @description 입력 필드에 성공 스타일 및 메시지를 적용합니다.
     * @param {JQuery} $input - 대상 입력 요소
     * @param {JQuery} $msgBox - 메시지를 출력할 요소
     * @param {string} msg - 출력할 성공 메시지
     */
    function showSuccess($input: JQuery, $msgBox: JQuery, msg: string): void {
        $input.removeClass('field-error-border').addClass('field-success-border');
        $msgBox.text(msg).removeClass('field-error').addClass('field-success');
    }

    /**
     * @function checkFinalValidation
     * @description 폼 제출 직전 모든 필드의 유효성 상태를 최종 확인합니다.
     * @returns {boolean} 모든 검증을 통과했을 경우 true를 반환합니다.
     */
    function checkFinalValidation(): boolean {
        if (!status.email) {
            Show.error("이메일 중복 확인을 해주세요.");
            $emailInput.trigger('focus');
            return false;
        }
        if (!status.password || !status.passwordCheck) {
            Show.error("비밀번호를 확인해주세요.");
            $pwInput.trigger('focus');
            return false;
        }
        if (!status.nickname) {
            Show.error("닉네임을 확인해주세요.");
            $nicknameInput.trigger('focus');
            return false;
        }
        return true;
    }

    /**
     * @function handleApiError
     * @description 서버 API 응답 에러 코드에 따라 적절한 UI 처리를 수행합니다.
     * @param {ApiErrorResponse} error - 서버에서 전달된 에러 객체
     */
    function handleApiError(error: ApiErrorResponse): void {
        if (error.code === 'VAL-001') {
            const pattern = /\[(.*?): (.*?)]/g;
            let match;
            let firstInput: JQuery | null = null;

            while ((match = pattern.exec(error.message)) !== null) {
                const fieldName = match[1];
                const msg = match[2];

                let inputId = fieldName;
                let msgBoxId = fieldName + '-msg';

                if (fieldName === 'rawPassword') {
                    inputId = 'rawPassword';
                    msgBoxId = 'rawPassword-msg';
                } else if (fieldName === 'passwordCheck') {
                    inputId = 'passwordCheck';
                    msgBoxId = 'password-check-msg';
                }

                const $input = $('#' + inputId);
                const $msgBox = $('#' + msgBoxId);

                if ($input.length) {
                    showError($input, $msgBox, msg);
                    if (!firstInput) firstInput = $input;
                }
            }
            if (firstInput) firstInput.trigger('focus');
        } else if (error.code === 'MEM-002') {
            showError($emailInput, $('#email-msg'), error.message);
            status.email = false;
            $emailInput.trigger('focus');
        } else {
            Show.error(error.message);
        }
    }
});