/**
 * @file write.ts
 * @package artist/funding
 * @description 크라우드 펀딩 생성 및 제안 폼을 제어하며, 날짜 선택기(Flatpickr) 및 이미지 업로드를 연동하는 모듈입니다.
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

    const isArtist = $('#isArtist').val() === 'true';
    const artistId = $('#artistId').val() as string;
    let uploadedFileId: number | null = null;

    // ===================================================================================
    //  SECTION: Input Handlers (Amount & Dates)
    // ===================================================================================

    /**
     * @description 목표 금액 입력 시 숫자가 아닌 문자를 제거하고 3자리 콤마 포맷을 적용합니다.
     */
    $targetAmount.on('input', (e: JQuery.TriggeredEvent) => {
        const $target = $(e.currentTarget);
        let val = ($target.val() as string || '').replace(/[^0-9]/g, '');

        if (val) {
            $target.val(Number(val).toLocaleString());
        } else {
            $target.val('');
        }
    });

    /**
     * @description 진행 기간 선택 달력(Flatpickr)을 초기화합니다.
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
                $fileInput.val('');
                $previewImg.hide();
                $uploadPlaceholder.show();
                $uploadBox.removeClass('has-image');
                uploadedFileId = null;
            }
        });
    });

    // ===================================================================================
    //  SECTION: Form Submission Logic
    // ===================================================================================

    /**
     * @description 폼 제출 시 유효성 검사를 수행하고 서버에 펀딩 생성/제안을 요청합니다.
     */
    $form.on('submit', (e: JQuery.SubmitEvent) => {
        e.preventDefault();

        const title = ($('#title').val() as string).trim();
        const content = ($('#content').val() as string).trim();
        const rawAmount = ($targetAmount.val() as string).replace(/,/g, '');

        let startDate: string | null = null;
        let endDate: string | null = null;

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

        if (isArtist) {
            startDate = $('#startDate').val() as string;
            endDate = $('#endDate').val() as string;
            if (!startDate || !endDate) {
                Show.error('진행 기간을 정확히 선택해주세요.');
                return;
            }
        }

        if (!content) {
            Show.error('상세 내용을 입력해주세요.');
            $('#content').trigger('focus');
            return;
        }

        const payload = {
            artistId: artistId,
            title: title,
            content: content,
            targetAmount: parseInt(rawAmount, 10),
            startDate: startDate,
            endDate: endDate,
            fileId: uploadedFileId
        };

        const $btn = $('.btn-submit');
        const originalText = $btn.text();
        $btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i> 개설 중...');

        $.ajax({
            url: '/api/v1/crowd-funding',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(payload),
            success: () => {
                Show.success('펀딩이 성공적으로 생성되었습니다.');
                setTimeout(() => {
                    location.href = `/artist/${artistId}/funding`;
                }, 1000);
            },
            error: (xhr: JQuery.jqXHR) => {
                const res = xhr.responseJSON as ApiResponse;
                const msg = res?.error?.message || "생성 실패";
                Show.error(msg);
                $btn.prop('disabled', false).text(originalText);
            }
        });
    });
});