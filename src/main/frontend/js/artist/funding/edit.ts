/**
 * @file edit.ts
 * @package artist/funding
 * @description 팬이 제안한 펀딩을 아티스트가 검토, 수정하고 최종 승낙(오픈)하는 모듈입니다.
 */

import $ from 'jquery';
import {Show} from '@/util/toast';
import {ApiResponse} from '@/types/api';

import flatpickr from 'flatpickr';
import {Korean} from 'flatpickr/dist/l10n/ko.js';

$(() => {
    // ===================================================================================
    //  SECTION: DOM Elements & State Management
    // ===================================================================================

    const $form = $('#funding-form');
    const $fileInput = $('#file-input');
    const $previewImg = $('#preview-img');
    const $uploadPlaceholder = $('.upload-placeholder');
    const $uploadBox = $('.img-upload-box');
    const $targetAmount = $('#targetAmount');

    const originalFileIdVal = $('#originalFileId').val() as string;
    let uploadedFileId: number | null = originalFileIdVal ? parseInt(originalFileIdVal, 10) : null;

    // ===================================================================================
    //  SECTION: Formatting & Date Picker
    // ===================================================================================

    /**
     * @description 초기 렌더링 시 기존 목표 금액에 천 단위 콤마를 적용합니다.
     */
    const initialAmount = ($targetAmount.val() as string).replace(/[^0-9]/g, '');
    if (initialAmount) {
        $targetAmount.val(Number(initialAmount).toLocaleString());
    }

    /**
     * @description 목표 금액 입력 시 숫자 이외의 문자를 제거하고 콤마 포맷을 적용합니다.
     */
    $targetAmount.on('input', (e: JQuery.TriggeredEvent) => {
        const $target = $(e.currentTarget);
        const val = ($target.val() as string || '').replace(/[^0-9]/g, '');

        if (val) {
            $target.val(Number(val).toLocaleString());
        } else {
            $target.val('');
        }
    });

    /**
     * @description 펀딩 진행 기간 설정을 위한 달력 컴포넌트(Flatpickr)를 초기화합니다.
     */
    if ($('#dateRange').length > 0) {
        flatpickr("#dateRange", {
            mode: "range",
            dateFormat: "Y-m-d",
            locale: Korean,
            minDate: "today",
            showMonths: 1,
            onChange: (selectedDates: Date[], _dateStr: string, instance: flatpickr.Instance) => {
                if (selectedDates.length === 2) {
                    const start = instance.formatDate(selectedDates[0], "Y-m-d");
                    const end = instance.formatDate(selectedDates[1], "Y-m-d");

                    $('#startDate').val(start);
                    $('#endDate').val(end);

                    const diffTime = Math.abs(selectedDates[1].getTime() - selectedDates[0].getTime());
                    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
                    $('#date-help-text').text(`총 ${diffDays}일 동안 진행됩니다. (${start} ~ ${end})`).css('color', '#2563eb');
                } else {
                    $('#date-help-text').text("시작일과 종료일을 모두 선택해주세요.").css('color', '#64748b');
                    $('#startDate').val('');
                    $('#endDate').val('');
                }
            }
        });
    }

    // ===================================================================================
    //  SECTION: File Upload Logic
    // ===================================================================================

    $fileInput.on('click', (e: JQuery.ClickEvent) => e.stopPropagation());

    $uploadBox.on('click', () => {
        $fileInput.trigger('click');
    });

    /**
     * @description 파일 선택 시 비동기로 서버에 업로드하고 화면에 미리보기를 렌더링합니다.
     */
    $fileInput.on('change', (e: JQuery.ChangeEvent) => {
        const target = e.currentTarget as HTMLInputElement;
        const file = target.files?.[0];

        if (!file) return;

        const formData = new FormData();
        formData.append('file', file);

        $.ajax({
            url: '/api/v1/files/upload',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: (res: ApiResponse<{ id: number }> | any) => {
                uploadedFileId = res.data?.id || res.id;

                const reader = new FileReader();
                reader.onload = (event: ProgressEvent<FileReader>) => {
                    $previewImg.attr('src', event.target?.result as string).show();
                    $uploadPlaceholder.hide();
                    $uploadBox.addClass('has-image');
                };
                reader.readAsDataURL(file);

                Show.success('이미지가 업로드되었습니다.');
            },
            error: () => {
                Show.error('이미지 업로드에 실패했습니다.');
            }
        });
    });

    // ===================================================================================
    //  SECTION: Form Submission
    // ===================================================================================

    /**
     * @description 수정된 데이터를 검증하고 서버에 승낙(Accept) 및 수정을 요청합니다.
     */
    $form.on('submit', (e: JQuery.SubmitEvent) => {
        e.preventDefault();

        const fundingId = $('#fundingId').val() as string;
        const artistId = $('#artistId').val() as string;
        const title = ($('#title').val() as string).trim();
        const content = ($('#content').val() as string).trim();
        const rawAmount = ($targetAmount.val() as string).replace(/,/g, '');
        const startDate = $('#startDate').val() as string;
        const endDate = $('#endDate').val() as string;

        if (!title) {
            Show.error('프로젝트 제목을 입력해주세요.');
            $('#title').trigger('focus');
            return;
        }
        if (!rawAmount || parseInt(rawAmount, 10) < 1000) {
            Show.error('목표 금액은 최소 1,000원 이상이어야 합니다.');
            $targetAmount.trigger('focus');
            return;
        }
        if (!startDate || !endDate) {
            Show.error('진행 기간을 반드시 선택해야 합니다.');
            $('#dateRange').trigger('focus');
            return;
        }
        if (!content) {
            Show.error('상세 내용을 입력해주세요.');
            $('#content').trigger('focus');
            return;
        }

        const payload = {
            title: title,
            content: content,
            targetAmount: parseInt(rawAmount, 10),
            startDate: startDate,
            endDate: endDate,
            fileId: uploadedFileId
        };

        const $btn = $('.btn-submit');
        $btn.prop('disabled', true).text('오픈 중...');

        $.ajax({
            url: `/api/v1/crowd-funding/${fundingId}/edit-accept`,
            type: 'PATCH',
            contentType: 'application/json',
            data: JSON.stringify(payload),
            success: () => {
                Show.success('성공적으로 수정 및 오픈되었습니다.');
                setTimeout(() => {
                    location.href = `/artist/${artistId}/funding/${fundingId}`;
                }, 1000);
            },
            error: (xhr: JQuery.jqXHR) => {
                const res = xhr.responseJSON as ApiResponse;
                Show.error(res?.error?.message || "오픈 실패");
                $btn.prop('disabled', false).text('수정 후 펀딩 오픈하기');
            }
        });
    });
});