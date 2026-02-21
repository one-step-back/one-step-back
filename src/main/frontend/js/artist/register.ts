/**
 * @file register.ts
 * @package artist
 * @description 아티스트 채널 개설을 위한 폼 제출 및 배너 이미지 미리보기 기능을 제어하는 모듈입니다.
 */

import $ from 'jquery';
import {Show} from '@/util/toast';
import {ApiErrorResponse, ApiResponse} from '@/types/api';

$(() => {
    // ===================================================================================
    //  SECTION: DOM Elements & State Management
    // ===================================================================================

    const $form = $('#artistForm');
    const $fileInput = $('#blogImage');
    const $preview = $('#banner-preview');
    const $icon = $preview.find('.upload-icon-wrapper');

    const $nameInput = $('#blogName');
    const $descInput = $('#description');
    const $submitBtn = $('#btn-submit');

    // ===================================================================================
    //  SECTION: Image Preview Logic
    // ===================================================================================

    /**
     * @description 프리뷰 영역 클릭 시 숨겨진 파일 입력창의 클릭 이벤트를 트리거합니다.
     */
    $preview.on('click', () => {
        $fileInput.trigger('click');
    });

    /**
     * @description 파일이 선택되면 FileReader를 활용하여 배경 이미지 미리보기를 렌더링합니다.
     */
    $fileInput.on('change', (e: JQuery.ChangeEvent) => {
        const target = e.target as HTMLInputElement;
        const file = target.files?.[0];

        if (file) {
            const reader = new FileReader();
            reader.onload = (event: ProgressEvent<FileReader>) => {
                $icon.hide();
                $preview.find('.preview-img').remove();

                const result = event.target?.result as string;
                const imgTag = `<img src="${result}" class="preview-img" alt="Banner Preview">`;

                $preview.append(imgTag);
                $preview.addClass('has-image');
            };
            reader.readAsDataURL(file);
        } else {
            $preview.find('.preview-img').remove();
            $icon.show();
            $preview.removeClass('has-image');
        }
    });

    // ===================================================================================
    //  SECTION: Form Submission Logic
    // ===================================================================================

    /**
     * @description 채널 생성 폼 데이터를 서버로 전송합니다.
     */
    $form.on('submit', function (e: JQuery.SubmitEvent) {
        e.preventDefault();

        clearValidationErrors();

        if (!validateForm()) return;

        const formData = new FormData(this as HTMLFormElement);
        const originalText = $submitBtn.text();
        $submitBtn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i> 개설 중...');

        $.ajax({
            url: '/api/v1/artists/register',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: (response: ApiResponse<number>) => {
                if (response.success && response.data) {
                    const artistId = response.data;
                    window.location.href = `/artist/${artistId}`;
                } else if (response.error) {
                    handleApiError(response.error);
                    $submitBtn.prop('disabled', false).text(originalText);
                }
            },
            error: (xhr: JQuery.jqXHR) => {
                console.error("Artist Register Error:", xhr);
                Show.error("채널 생성 중 오류가 발생했습니다.");
                $submitBtn.prop('disabled', false).text(originalText);
            }
        });
    });

    // ===================================================================================
    //  SECTION: Helper Functions
    // ===================================================================================

    /**
     * @function clearValidationErrors
     * @description 이전 폼 제출 시 발생했던 유효성 검사 에러 UI를 초기화합니다.
     */
    function clearValidationErrors(): void {
        $('.field-error-border').removeClass('field-error-border');
        $('.validation-msg').text('').removeClass('field-error');
    }

    /**
     * @function validateForm
     * @description 폼 제출 전 클라이언트 측 유효성 검사를 수행합니다.
     * @returns {boolean} 유효성 검사 통과 여부
     */
    function validateForm(): boolean {
        const name = ($nameInput.val() as string).trim();
        const desc = ($descInput.val() as string).trim();
        const target = $fileInput[0] as HTMLInputElement;
        const file = target.files?.[0];

        if (!file) {
            Show.error("채널을 대표할 배너 이미지를 등록해주세요.");
            return false;
        }
        if (!name) {
            Show.error("채널 이름을 입력해주세요.");
            $nameInput.trigger('focus');
            return false;
        }
        if (name.length < 2 || name.length > 20) {
            Show.error("채널 이름은 2~20자 사이여야 합니다.");
            $nameInput.trigger('focus');
            return false;
        }
        if (!desc) {
            Show.error("소개글을 입력해주세요.");
            $descInput.trigger('focus');
            return false;
        }
        return true;
    }

    /**
     * @function handleApiError
     * @description 서버 API에서 반환된 에러 코드를 파싱하여 적절한 UI로 출력합니다.
     * @param {ApiErrorResponse} error - 서버에서 전달된 표준 에러 응답 객체
     */
    function handleApiError(error: ApiErrorResponse): void {
        if (error.code === 'VAL-001') {
            const pattern = /\[(.*?): (.*?)]/g;
            let match;

            while ((match = pattern.exec(error.message)) !== null) {
                const fieldName = match[1];
                const msg = match[2];
                const $input = $('#' + fieldName);
                const $msgBox = $('#' + fieldName + '-msg');

                if ($input.length) {
                    $input.addClass('field-error-border');
                    $msgBox.text(msg).addClass('field-error');
                }
            }
        } else {
            Show.error(error.message);
        }
    }
});