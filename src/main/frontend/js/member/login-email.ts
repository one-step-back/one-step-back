/**
 * @file login-email.ts
 * @package member
 * @description 이메일 로그인 페이지의 유효성 검사 및 REST API 전송 로직 (TypeScript)
 */

import $ from 'jquery';
import { Show } from '../util/toast';

/** * @interface ApiErrorResponse
 * 서버에서 내려주는 공통 에러 응답 구조
 */
interface ApiErrorResponse {
    code: string;
    message: string;
}

$(() => {
    /** @section DOM Elements - 폼 요소 추출 */
    const $loginForm = $('#loginForm');
    const $emailInput = $('#email');
    const $pwInput = $('#password');

    /**
     * @function validateForm
     * @description 클라이언트 사이드 기본 입력값 검증
     * @returns {boolean} 검증 통과 여부
     */
    const validateForm = (): boolean => {
        const email = $emailInput.val() as string;
        const pw = $pwInput.val() as string;

        if (!email.trim()) {
            Show.error("이메일을 입력해주세요.");
            $emailInput.trigger("focus");
            return false;
        }
        if (!pw.trim()) {
            Show.error("비밀번호를 입력해주세요.");
            $pwInput.trigger("focus");
            return false;
        }
        return true;
    };

    /**
     * @function showError
     * @description 필드별 에러 스타일 및 메시지 노출 처리
     */
    const showError = ($input: JQuery, $msgBox: JQuery, msg: string): void => {
        $input.addClass('field-error-border');
        if ($msgBox.length) {
            $msgBox.text(msg).addClass('field-error');
        }
    };

    /**
     * @function handleApiError
     * @description 서버 API 응답 에러 코드별 분기 처리
     * @param {ApiErrorResponse} error 서버에서 전달된 에러 객체
     */
    const handleApiError = (error: ApiErrorResponse): void => {
        /** * [CASE 1] VAL-001: 입력값 형식 유효성 에러 (서버 검증 실패) */
        if (error.code === 'VAL-001') {
            const pattern = /\[(.*?): (.*?)\]/g;
            let match;
            let firstInput: JQuery | null = null;

            while ((match = pattern.exec(error.message)) !== null) {
                const fieldName = match[1]; // 예: email, password
                const msg = match[2];

                const $input = $('#' + fieldName);
                const $msgBox = $('#' + fieldName + '-msg');

                if ($input.length) {
                    showError($input, $msgBox, msg);
                    if (!firstInput) firstInput = $input;
                }
            }
            if (firstInput) firstInput.focus();
        }
        /** * [CASE 2] MEM-003 or LOGIN_FAILED: 아이디/비밀번호 불일치 */
        else {
            Show.error(error.message);

            if (error.code === 'MEM-003' || error.code === 'LOGIN_FAILED') {
                $pwInput.val('').focus();
                // 상남자의 피드백: 폼 흔들기 애니메이션
                $loginForm.addClass('shake');
                setTimeout(() => $loginForm.removeClass('shake'), 500);
            }
        }
    };

    /** @section Event Handlers - 폼 제출 이벤트 바인딩 */
    $loginForm.on('submit', (e: JQuery.SubmitEvent) => {
        e.preventDefault();

        // 1. 기본 검증 실행
        if (!validateForm()) return;

        // 2. 전송 데이터 준비
        const data = {
            email: $emailInput.val(),
            password: $pwInput.val()
        };

        // 3. REST API 전송
        $.ajax({
            url: '/api/v1/members/login',
            type: 'POST',
            data: JSON.stringify(data),
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: () => {
                // 로그인 성공 시 메인으로 이동
                window.location.href = '/';
            },
            error: (xhr) => {
                const response = xhr.responseJSON;

                if (response && response.error) {
                    handleApiError(response.error);
                } else {
                    console.error("Login Error:", xhr);
                    Show.error("서버 통신 중 알 수 없는 오류가 발생했습니다.");
                }
            }
        });
    });
});