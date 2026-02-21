/**
 * @file artist.ts
 * @package artist/setting
 * @description 아티스트 채널 설정 탭의 프로필 이미지 미리보기 및 폼 저장 기능을 제어합니다.
 */

import $ from 'jquery';
import { Show } from '@/util/toast';
import { ApiResponse } from '@/types/api';

export function initArtistTab(): void {
    const readURL = (input: HTMLInputElement, $previewTarget: JQuery): void => {
        if (input.files && input.files[0]) {
            const reader = new FileReader();
            reader.onload = (e: ProgressEvent<FileReader>) => {
                const result = e.target?.result as string;
                $previewTarget.css('background-image', `url(${result})`);
            };
            reader.readAsDataURL(input.files[0]);
        }
    };

    $('#bg-file').on('change', function (this: HTMLInputElement) {
        readURL(this, $('#bg-preview'));
    });

    $('#profile-file').on('change', function (this: HTMLInputElement) {
        readURL(this, $('#profile-preview'));
    });

    $('#btn-save-artist').on('click', function (e: JQuery.ClickEvent) {
        const $btn = $(e.currentTarget);
        const formElement = $('#artist-form')[0] as HTMLFormElement;
        const formData = new FormData(formElement);

        const originalText = $btn.text();
        $btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i> 저장 중...');

        $.ajax({
            url: '/api/v1/artists',
            type: 'PUT',
            data: formData,
            processData: false,
            contentType: false,
            success: (res: ApiResponse) => {
                Show.success('채널 정보가 성공적으로 수정되었습니다.');
                $btn.prop('disabled', false).text(originalText);
            },
            error: (xhr: JQuery.jqXHR) => {
                console.error(xhr);
                const res = xhr.responseJSON as ApiResponse;
                const msg = res?.error?.message || '수정에 실패했습니다. 입력값을 확인해주세요.';
                Show.error(msg);
                $btn.prop('disabled', false).text(originalText);
            }
        });
    });
}