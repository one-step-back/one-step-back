/**
 * @file membership.ts
 * @package artist/setting
 * @description 아티스트 멤버십 설정 탭의 조회, 생성, 수정, 삭제 및 상태 변경 기능을 제어합니다.
 */

import $ from 'jquery';
import { Show } from '@/util/toast';
import { Confirm } from '@/util/confirm';
import { ApiResponse } from '@/types/api';

interface MembershipTier {
    id: number | null;
    name: string;
    price: number;
    description: string;
    levelOrder: number;
    imagePath?: string | null;
    imageId?: number | null;
    active: boolean;
    subscriberCount: number;
}

interface MembershipData {
    hasAppliedBankAccount: boolean;
    memberships: MembershipTier[];
}

// ===================================================================================
//  Module Level State & DOM Cache
// ===================================================================================
let $container: JQuery;
let $modal: JQuery;
let $btnClose: JQuery;
let $btnSave: JQuery;
let $modalTitle: JQuery;
let $inputFile: JQuery;
let $previewContainer: JQuery;

let currentData: MembershipData = { hasAppliedBankAccount: false, memberships: [] };
let uploadedImageId: number | null = null;
let originalImageId: number | null = null;
let deleteImageFlag: boolean = false;
let isEditMode: boolean = false;
let editingMembershipId: number | null = null;

// ===================================================================================
//  Core Functions
// ===================================================================================

const renderBlocker = (): void => {
    const blockerHtml = `
        <div class="account-blocker">
            <div class="account-blocker__icon"><i class="fas fa-university"></i></div>
            <h3>정산 계좌가 등록되지 않았습니다</h3>
            <p>멤버십 기능을 사용하려면 정산 계좌 등록이 필수입니다.</p>
            <button type="button" class="btn btn--primary" id="btn-go-account">계좌 설정하러 가기</button>
        </div>`;
    $container.html(blockerHtml);
    $('#btn-go-account').on('click', () => $('.tab-item[data-tab="account"]').trigger('click'));
};

const renderPreview = (imageUrl: string, showDeleteBtn: boolean): void => {
    const deleteBtnHtml = showDeleteBtn ? '<button type="button" class="btn-remove-image" style="position:absolute; top:5px; right:5px; background:rgba(0,0,0,0.6); color:white; border:none; border-radius:50%; width:24px; height:24px; cursor:pointer;"><i class="fas fa-times"></i></button>' : '';
    $previewContainer.html(`<div class="upload-preview" style="background-image:url('${imageUrl}'); position:relative; background-size:cover; background-position:center; height:150px; border-radius:8px; margin-top:10px;"><div class="upload-preview-overlay" style="position:absolute; bottom:0; left:0; right:0; background:rgba(0,0,0,0.5); color:white; font-size:12px; padding:4px; text-align:center;">${showDeleteBtn ? '이미지 등록됨' : '현재 이미지'}</div>${deleteBtnHtml}</div>`).show();
};

const openModal = (level: number, mode: 'CREATE' | 'EDIT'): void => {
    $('#input-level').val(level);
    $inputFile.val('');
    $previewContainer.empty().hide();
    uploadedImageId = null;
    originalImageId = null;
    deleteImageFlag = false;

    if (mode === 'EDIT') {
        isEditMode = true;
        const membership = currentData.memberships.find(m => m.levelOrder === level);
        if (!membership) return;

        editingMembershipId = membership.id;

        $modalTitle.text(`Tier ${level} 멤버십 수정`);
        $('#input-name').val(membership.name);
        $('#input-price').val(membership.price);
        $('#input-desc').val(membership.description);

        if (membership.subscriberCount > 0) {
            $('#input-price').prop('disabled', true);
            if ($('#price-warning').length === 0) {
                $('#input-price').after('<p id="price-warning" style="color:orange; font-size:12px; margin-top:5px;">활성 구독자가 있어 가격 수정 불가</p>');
            }
        } else {
            $('#input-price').prop('disabled', false);
            $('#price-warning').remove();
        }

        if (membership.imagePath) {
            originalImageId = membership.imageId || null;
            const imageUrl = membership.imagePath.startsWith('/') ? membership.imagePath : `/api/v1/files/${membership.imagePath}`;
            renderPreview(imageUrl, true);
        }
        $btnSave.text('수정 완료');
    } else {
        isEditMode = false;
        editingMembershipId = null;
        $modalTitle.text(`Tier ${level} 멤버십 생성`);
        $('#input-name').val('');
        $('#input-price').val('').prop('disabled', false);
        $('#input-desc').val('');
        $('#price-warning').remove();
        $btnSave.text('생성하기');
    }
    $modal.addClass('open');
};

const closeModal = (): void => { $modal.removeClass('open'); };

const deleteMembership = (level: number): void => {
    const membership = currentData.memberships.find(m => m.levelOrder === level);
    if (!membership) return;

    if (membership.subscriberCount > 0) {
        Show.error('활성 구독자가 있어 삭제할 수 없습니다. 비활성화를 이용해주세요.');
        return;
    }

    Confirm.open({
        title: "멤버십 삭제",
        desc: `Tier ${level} 멤버십을 영구 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.`,
        actionText: "삭제하기",
        onConfirm: () => {
            const previousMemberships = JSON.parse(JSON.stringify(currentData.memberships));

            currentData.memberships = currentData.memberships.filter(m => m.levelOrder !== level);
            renderContent(currentData);

            $.ajax({
                url: `/api/v1/memberships/${membership.id}`,
                type: 'DELETE',
                success: () => {
                    Show.success('멤버십이 삭제되었습니다.');
                },
                error: (xhr: JQuery.jqXHR) => {
                    const res = xhr.responseJSON as ApiResponse;
                    Show.error('삭제 실패: ' + (res?.error?.message || '오류가 발생했습니다.'));
                    currentData.memberships = previousMemberships;
                    renderContent(currentData);
                }
            });
        }
    });
};

const toggleMembershipStatus = (level: number, isActive: boolean, $checkbox: JQuery): void => {
    const membership = currentData.memberships.find(m => m.levelOrder === level);
    if (!membership || !membership.id) return;

    $.ajax({
        url: `/api/v1/memberships/${membership.id}/status`,
        type: 'PATCH',
        contentType: 'application/json',
        data: JSON.stringify({ active: isActive }),
        success: () => {
            membership.active = isActive;
            renderContent(currentData);
            Show.success(`멤버십이 ${isActive ? '활성화' : '비활성화'} 되었습니다.`);
        },
        error: () => {
            Show.error('상태 변경에 실패했습니다.');
            $checkbox.prop('checked', !isActive);
            renderContent(currentData);
        }
    });
};

const bindCardEvents = (): void => {
    $('.btn-open-modal').on('click', function () { openModal($(this).data('level') as number, 'CREATE'); });
    $('.btn-edit-membership').on('click', function () { openModal($(this).data('level') as number, 'EDIT'); });

    $('.btn-delete-membership').on('click', function () {
        deleteMembership($(this).data('level') as number);
    });

    $('.btn-toggle-active').on('change', function () {
        const $checkbox = $(this);
        toggleMembershipStatus($checkbox.data('level') as number, $checkbox.is(':checked'), $checkbox);
    });
};

const getCardHtml = (level: number, memberships: MembershipTier[]): string => {
    const membership = memberships.find(m => m.levelOrder === level);
    if (membership) {
        const imageUrl = membership.imagePath ? (membership.imagePath.startsWith('/') ? membership.imagePath : `/api/v1/files/${membership.imagePath}`) : null;
        const imageHtml = imageUrl ? `<div class="card-image" style="background-image:url('${imageUrl}');"></div>` : '';
        const statusClass = membership.active ? 'status-active' : 'status-inactive';
        const statusText = membership.active ? '운영 중' : '비활성';
        const toggleChecked = membership.active ? 'checked' : '';

        return `
            <div class="mock-card registered" id="card-level-${level}">
                <div class="card-header-row">
                    <div class="card-badge">Tier ${level}</div>
                    <div class="status-indicator ${statusClass}">● ${statusText}</div>
                </div>
                ${imageHtml}
                <h3 class="card-title">${membership.name}</h3>
                <p class="card-price">₩ ${membership.price.toLocaleString()} <span>/월</span></p>
                <p class="card-subscriber-count">구독자: <b>${membership.subscriberCount || 0}명</b></p>
                <p class="card-description">${membership.description}</p>
                <div class="card-actions">
                    <label class="switch-wrapper" title="활성/비활성">
                        <input type="checkbox" class="btn-toggle-active" data-level="${level}" ${toggleChecked}>
                        <span class="slider-round"></span>
                    </label>
                    <button class="btn btn--secondary btn-card-action btn-edit-membership" data-level="${level}">수정</button>
                    <button class="btn btn--outline btn-card-action btn-card-delete btn-delete-membership" data-level="${level}">삭제</button>
                </div>
            </div>`;
    } else {
        return `
            <div class="mock-card empty" id="card-level-${level}">
                <div class="empty-icon">+</div>
                <h3 class="empty-title">Tier ${level}</h3>
                <p class="empty-desc">미설정</p>
                <button class="btn btn--outline btn-open-modal" data-level="${level}">생성하기</button>
            </div>`;
    }
};

const renderContent = (data: MembershipData): void => {
    $container.empty();
    if (!data.hasAppliedBankAccount) {
        renderBlocker();
        return;
    }
    let gridHtml = '<div class="mock-membership-grid">';
    for (let level = 1; level <= 3; level++) {
        gridHtml += getCardHtml(level, data.memberships);
    }
    gridHtml += '</div>';
    $container.html(gridHtml);
    bindCardEvents();
};

export const loadMembershipData = (): void => {
    $container.html('<div class="loading-spinner"><i class="fas fa-circle-notch fa-spin"></i></div>');
    $.ajax({
        url: '/api/v1/memberships/manage',
        type: 'GET',
        success: (res: ApiResponse<MembershipData>) => {
            currentData = res.data || res as unknown as MembershipData;
            renderContent(currentData);
        },
        error: () => {
            $container.html('<p style="text-align:center; color:red;">데이터 로드 실패</p>');
        }
    });
};

// ===================================================================================
//  Initialization
// ===================================================================================

export function initMembershipTab(): void {
    $container = $('#membership-content-area');
    $modal = $('#membership-modal');
    $btnClose = $('.btn-close-modal');
    $btnSave = $('#btn-save-membership');
    $modalTitle = $('#modal-title');
    $inputFile = $('#input-file');

    if ($('#membership-image-preview').length === 0) {
        $inputFile.parent().append('<div id="membership-image-preview" style="margin-top:10px; display:none;"></div>');
    }
    $previewContainer = $('#membership-image-preview');

    $btnClose.on('click', () => closeModal());

    $inputFile.on('change', function (this: HTMLInputElement) {
        const file = this.files?.[0];
        if (!file) return;

        const formData = new FormData();
        formData.append('file', file);

        $previewContainer.html('<div class="upload-loading"><i class="fas fa-spinner fa-spin"></i> 업로드 중...</div>').show();

        $.ajax({
            url: '/api/v1/files/upload',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: (res: ApiResponse<{ id: number }>) => {
                const fileId = res.data?.id || (res as any).id;
                uploadedImageId = fileId;
                deleteImageFlag = false;
                const imageUrl = `/api/v1/files/${fileId}`;
                renderPreview(imageUrl, true);
            },
            error: () => {
                Show.error('이미지 업로드에 실패했습니다.');
                $inputFile.val('');
                $previewContainer.hide();
                uploadedImageId = null;
            }
        });
    });

    $previewContainer.on('click', '.btn-remove-image', () => {
        $inputFile.val('');
        $previewContainer.empty().hide();
        deleteImageFlag = true;
        uploadedImageId = null;
    });

    $btnSave.on('click', () => {
        const level = parseInt($('#input-level').val() as string, 10);
        const name = $('#input-name').val() as string;
        let price = parseInt($('#input-price').val() as string, 10);
        const desc = $('#input-desc').val() as string;

        if (isEditMode && $('#input-price').prop('disabled')) {
            const target = currentData.memberships.find(m => m.levelOrder === level);
            if (target) price = target.price;
        }

        if (!name || !price || !desc) {
            Show.error('모든 필수 정보를 입력해주세요.');
            return;
        }

        let payload: any = {};
        const targetMembership = currentData.memberships.find(m => m.levelOrder === level);
        let displayImagePath = uploadedImageId ? `/api/v1/files/${uploadedImageId}` : (!deleteImageFlag && originalImageId && targetMembership ? targetMembership.imagePath : null);

        if (isEditMode) {
            payload = { name, price, description: desc, levelOrder: level, currentImageId: originalImageId, newImageId: uploadedImageId, deleteImage: deleteImageFlag };
        } else {
            payload = { name, price, description: desc, levelOrder: level, imageId: uploadedImageId };
        }

        const previousMemberships = JSON.parse(JSON.stringify(currentData.memberships));

        if (isEditMode) {
            const index = currentData.memberships.findIndex(m => m.levelOrder === level);
            if (index !== -1) {
                currentData.memberships[index] = { ...currentData.memberships[index], name, price, description: desc, imagePath: displayImagePath };
            }
        } else {
            currentData.memberships.push({ id: null, name, price, description: desc, levelOrder: level, imagePath: displayImagePath, active: true, subscriberCount: 0 });
        }
        closeModal();
        renderContent(currentData);

        const url = isEditMode ? `/api/v1/memberships/${editingMembershipId}` : '/api/v1/memberships';
        const method = isEditMode ? 'PUT' : 'POST';

        $.ajax({
            url: url,
            type: method,
            contentType: 'application/json',
            data: JSON.stringify(payload),
            success: () => {
                loadMembershipData();
                Show.success(isEditMode ? '멤버십이 수정되었습니다.' : '멤버십이 생성되었습니다.');
            },
            error: (xhr: JQuery.jqXHR) => {
                const res = xhr.responseJSON as ApiResponse;
                Show.error('저장 실패: ' + (res?.error?.message || '오류 발생'));
                currentData.memberships = previousMemberships;
                renderContent(currentData);
                openModal(level, isEditMode ? 'EDIT' : 'CREATE');
            }
        });
    });
}