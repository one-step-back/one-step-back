/**
 * @file feed-write.ts
 * @package artist/feed
 * @description 피드 작성 및 수정 기능을 제어하며, 동적 멤버십 티어 렌더링 및 다중 파일 업로드를 처리하는 모듈입니다.
 */

import $ from 'jquery';
import { Show } from '@/util/toast';
import { ApiResponse } from '@/types/api';

/**
 * @interface MembershipTier
 * @description 서버로부터 수신되는 멤버십 티어의 데이터 구조입니다.
 */
interface MembershipTier {
    id: number;
    name: string;
    levelOrder: number;
}

/**
 * @interface FileUploadResponse
 * @description 단일 파일 업로드 성공 시 반환되는 데이터 구조입니다.
 */
interface FileUploadResponse {
    id: number;
}

$(() => {
    // ===================================================================================
    //  SECTION: DOM Elements & State Management
    // ===================================================================================
    const $fileInput = $('#file-input');
    const $addMediaBtn = $('#btn-add-media');
    const $submitBtn = $('#btn-submit-feed');
    const $statusRadios = $('input[name="status"]');
    const $tierArea = $('#tier-selection-area');
    const $tierContainer = $('#tier-options-container');

    const mode = $('#form-mode').val() as string;
    const feedId = $('#feed-id').val() as string;
    const artistId = $('#artist-id').val() as string;
    const currentMinTier = parseInt($('#current-min-tier').val() as string, 10) || 0;

    let membershipCache: MembershipTier[] = [];
    let uploadedFileIds: number[] = [];
    let deleteFileIds: number[] = [];

    // ===================================================================================
    //  SECTION: Membership Logic
    // ===================================================================================

    /**
     * @function loadMemberships
     * @description 아티스트의 멤버십 목록을 서버로부터 조회하고 레벨 순으로 정렬하여 캐시합니다.
     */
    const loadMemberships = (): void => {
        $.ajax({
            url: '/api/v1/memberships',
            type: 'GET',
            data: { 'artist-id': artistId },
            success: (response: ApiResponse<MembershipTier[]>) => {
                // ApiResponse 도입 과도기를 위한 방어 코드
                const memberships = response.data || (response as unknown as MembershipTier[]);
                if (Array.isArray(memberships)) {
                    membershipCache = memberships.sort((a, b) => a.levelOrder - b.levelOrder);
                }
                renderTierOptions();
            },
            error: (xhr: JQuery.jqXHR) => {
                console.error("Membership Load Error:", xhr);
                $tierContainer.html('<div class="empty-text error">멤버십 정보를 불러오지 못했습니다.</div>');
            }
        });
    };

    /**
     * @function renderTierOptions
     * @description 캐시된 멤버십 데이터를 기반으로 권한 설정 UI를 렌더링합니다.
     */
    const renderTierOptions = (): void => {
        if (!membershipCache || membershipCache.length === 0) {
            $tierContainer.html('<div class="empty-text">등록된 멤버십이 없습니다.</div>');
            return;
        }

        $tierContainer.empty();

        membershipCache.forEach(m => {
            const isChecked = (currentMinTier === m.levelOrder) ? 'checked' : '';

            const html = `
                <label class="tier-radio">
                    <input type="radio" name="minTier" value="${m.levelOrder}" ${isChecked}>
                    <div class="tier-card">
                        <span class="tier-name">${m.name}</span>
                        <span class="tier-badge">Lv.${m.levelOrder}</span>
                    </div>
                </label>
            `;
            $tierContainer.append(html);
        });
    };

    /**
     * @function checkTierVisibility
     * @description 공개 상태(status)에 따라 멤버십 티어 선택 영역의 활성화 여부를 제어합니다.
     */
    const checkTierVisibility = (): void => {
        const currentStatus = $('input[name="status"]:checked').val() as string;
        if (currentStatus === 'MEMBER_ONLY') {
            $tierArea.addClass('active');
        } else {
            $tierArea.removeClass('active');
        }
    };

    $statusRadios.on('change', checkTierVisibility);

    // ===================================================================================
    //  SECTION: File Upload Logic
    // ===================================================================================

    $addMediaBtn.on('click', () => {
        $fileInput.trigger('click');
    });

    $fileInput.on('change', (e: JQuery.ChangeEvent) => {
        const target = e.target as HTMLInputElement;
        if (!target.files || target.files.length === 0) return;

        const files = Array.from(target.files);
        files.forEach(file => {
            uploadFileAndShowPreview(file);
        });
        $(target).val('');
    });

    $(document).on('click', '.btn-remove-existing', function (e: JQuery.ClickEvent) {
        const $item = $(e.currentTarget).closest('.media-item');
        const fileId = $item.data('file-id') as number;
        if (fileId) {
            deleteFileIds.push(fileId);
        }
        $item.remove();
    });

    /**
     * @function uploadFileAndShowPreview
     * @description 선택된 파일을 비동기로 서버에 업로드하고 클라이언트 측 미리보기를 제공합니다.
     * @param {File} file - 업로드할 파일 객체
     */
    const uploadFileAndShowPreview = (file: File): void => {
        const $item = $('<div class="media-item new-file"></div>');
        const $progressOverlay = $(`
            <div class="progress-overlay">
                <div class="progress-bar-container"><div class="progress-bar"></div></div>
                <span class="progress-text">0%</span>
            </div>
        `);
        $item.append($progressOverlay);
        $addMediaBtn.before($item);

        const fileUrl = URL.createObjectURL(file);
        const isVideo = file.type.startsWith('video');
        let $content: JQuery;

        if (isVideo) {
            $content = $('<video></video>').attr('src', fileUrl)
                .prop('muted', true).prop('playsinline', true)
                .css({ 'width': '100%', 'height': '100%', 'object-fit': 'cover', 'pointer-events': 'none' });
            $content.on('loadeddata', function (this: HTMLVideoElement) {
                this.currentTime = 0.1;
            });
            $item.append('<div class="media-item__video-icon"><i class="fas fa-play"></i></div>');
        } else {
            $content = $('<img>').attr('src', fileUrl);
        }
        $item.prepend($content);

        const formData = new FormData();
        formData.append('file', file);

        $.ajax({
            url: '/api/v1/files/upload',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            xhr: () => {
                const xhr = new window.XMLHttpRequest();
                xhr.upload.addEventListener("progress", (evt: ProgressEvent) => {
                    if (evt.lengthComputable) {
                        const percent = Math.round((evt.loaded / evt.total) * 100);
                        $progressOverlay.find('.progress-bar').css('width', percent + '%');
                        $progressOverlay.find('.progress-text').text(percent + '%');
                    }
                }, false);
                return xhr;
            },
            success: (res: ApiResponse<FileUploadResponse>) => {
                const fileId = res.data?.id || (res as any).id;
                $progressOverlay.fadeOut(300, function () {
                    $(this).remove();
                });
                $item.data('file-id', fileId);
                uploadedFileIds.push(fileId);

                const $removeBtn = $('<button type="button" class="media-item__remove-btn"><i class="fas fa-times"></i></button>');
                $removeBtn.on('click', () => {
                    $item.remove();
                    uploadedFileIds = uploadedFileIds.filter(id => id !== fileId);
                    URL.revokeObjectURL(fileUrl);
                });
                $item.append($removeBtn);
            },
            error: () => {
                Show.error(`파일 업로드 실패: ${file.name}`);
                $item.remove();
            }
        });
    };

    // ===================================================================================
    //  SECTION: Submit Logic
    // ===================================================================================

    $submitBtn.on('click', function (e: JQuery.ClickEvent) {
        const $btn = $(e.currentTarget);

        const category = $('select[name="category"]').val() as string;
        const title = ($('input[name="title"]').val() as string).trim();
        const content = ($('textarea[name="content"]').val() as string).trim();
        const status = $('input[name="status"]:checked').val() as string;
        const minTierVal = $('input[name="minTier"]:checked').val() as string;
        const minTier = minTierVal ? parseInt(minTierVal, 10) : 0;

        if ($('.progress-overlay').length > 0) {
            Show.error('파일 업로드가 진행 중입니다.');
            return;
        }

        if (!category) {
            Show.error('카테고리를 선택해주세요.');
            $('select[name="category"]').trigger('focus');
            return;
        }

        if (status === 'MEMBER_ONLY') {
            if (membershipCache.length === 0) {
                Show.error('등록된 멤버십이 없어 전용 글을 작성할 수 없습니다.');
                return;
            }
            if (!minTierVal) {
                Show.error('열람 가능한 최소 멤버십 등급을 선택해주세요.');
                $tierContainer.css('border', '1px solid red');
                setTimeout(() => $tierContainer.css('border', 'none'), 2000);
                return;
            }
        }

        const existingCount = $('.media-item.existing-file').length;
        if (!content && uploadedFileIds.length === 0 && existingCount === 0) {
            Show.error('내용을 입력하거나 파일을 추가해주세요.');
            return;
        }

        const feedData = {
            id: mode === 'edit' ? feedId : null,
            title: title,
            content: content,
            category: category,
            status: status,
            minTier: minTier,
            fileIds: uploadedFileIds,
            deleteFileIds: deleteFileIds
        };

        const method = (mode === 'edit') ? 'PUT' : 'POST';
        const url = (mode === 'edit') ? `/api/v1/feeds/${feedId}` : '/api/v1/feeds';

        $btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i> 처리 중...');

        $.ajax({
            url: url,
            type: method,
            contentType: 'application/json',
            data: JSON.stringify(feedData),
            success: (res: ApiResponse) => {
                Show.success(mode === 'edit' ? '게시물이 수정되었습니다.' : '게시물이 등록되었습니다.');
                setTimeout(() => {
                    location.href = `/artist/${artistId}/feed`;
                }, 1000);
            },
            error: (xhr: JQuery.jqXHR) => {
                console.error("Submit Error:", xhr);
                const res = xhr.responseJSON as ApiResponse;
                const msg = res?.error?.message || '요청 처리에 실패했습니다.';
                Show.error(msg);
                $btn.prop('disabled', false).text(mode === 'edit' ? '수정하기' : '게시하기');
            }
        });
    });

    // ===================================================================================
    //  SECTION: Initialization
    // ===================================================================================
    loadMemberships();
    checkTierVisibility();
});