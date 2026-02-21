/**
 * @file my-following.ts
 * @package my-page
 * @description 마이페이지의 팔로우 관리 화면에서 아티스트 목록 조회 및 팔로우 상태 변경(Toggle)을 처리하는 모듈입니다.
 */

import $ from 'jquery';
import { Show } from '@/util/toast';
import { Confirm } from '@/util/confirm';
import { ApiResponse } from '@/types/api';

/**
 * @interface FollowArtist
 * @description 서버로부터 수신되는 개별 팔로우 아티스트의 데이터 구조입니다.
 */
interface FollowArtist {
    id: number;
    artistNickname: string;
    blogName: string;
    artistProfilePath: string;
}

$(() => {
    /** @section DOM Elements - UI 렌더링 및 제어를 위한 DOM 요소 참조 */
    const $grid = $('#following-grid');
    const $spinner = $('#loading-spinner');
    const $emptyState = $('#empty-state');
    const $sortSelect = $('#sort-select');

    // ===================================================================================
    //  SECTION: Initialization
    // ===================================================================================

    // 초기 화면 로드 시 리스트를 조회합니다.
    loadFollowings($sortSelect.val() as string);

    // 정렬 옵션 변경 시 리스트를 재조회합니다.
    $sortSelect.on('change', function () {
        loadFollowings($(this).val() as string);
    });

    // ===================================================================================
    //  SECTION: Core Logic
    // ===================================================================================

    /**
     * @function loadFollowings
     * @description 지정된 정렬 기준에 따라 팔로우 목록을 서버로부터 조회합니다.
     * @param {string} sortType - 정렬 기준 (예: LATEST, NAME, BLOG)
     */
    function loadFollowings(sortType: string): void {
        $spinner.show();
        $grid.hide();
        $emptyState.hide();

        $.ajax({
            url: '/api/v1/follows/list',
            type: 'GET',
            data: { sort: sortType },
            success: (res: ApiResponse<FollowArtist[]>) => {
                const list = res.data || [];
                renderList(list);
            },
            error: (xhr: JQuery.jqXHR) => {
                console.error('[Error] 팔로우 목록 조회 실패:', xhr);
                Show.error('목록을 불러오는데 실패했습니다.');
            },
            complete: () => {
                $spinner.hide();
            }
        });
    }

    /**
     * @function renderList
     * @description 조회된 데이터를 기반으로 화면에 아티스트 카드를 렌더링합니다.
     * @param {FollowArtist[]} list - 아티스트 정보 목록 배열
     */
    function renderList(list: FollowArtist[]): void {
        $grid.empty();

        if (!list || list.length === 0) {
            $emptyState.fadeIn();
            return;
        }

        let html = '';
        list.forEach(artist => {
            html += `
                <div class="artist-card" id="card-${artist.id}">
                    <div class="artist-avatar" 
                         style="background-image: url('${artist.artistProfilePath}');">
                    </div>
                    
                    <div class="artist-info">
                        <span class="artist-blog-name" title="${artist.blogName}">${artist.blogName}</span>
                        <span class="artist-nickname">@${artist.artistNickname}</span>
                    </div>
                    
                    <div class="card-actions">
                        <a href="/artist/${artist.id}" class="btn-visit">방문</a>
                        <button type="button" class="btn-toggle-follow btn-unfollow" 
                                data-id="${artist.id}" 
                                data-name="${artist.artistNickname}"
                                data-status="true">
                            취소
                        </button>
                    </div>
                </div>
            `;
        });

        $grid.html(html).fadeIn();
    }

    // ===================================================================================
    //  SECTION: Event Handlers
    // ===================================================================================

    /**
     * @description 팔로우 상태 변경(Toggle) 버튼 클릭 이벤트를 처리합니다.
     * 현재 상태가 '팔로우'인 경우 확인 모달을 호출하고, '언팔로우'인 경우 즉시 재팔로우를 수행합니다.
     */
    $(document).on('click', '.btn-toggle-follow', function () {
        const $btn = $(this);
        const artistId = $btn.data('id') as number;
        const artistName = $btn.data('name') as string;
        const currentStatus = $btn.data('status') as boolean;

        /**
         * @function executeToggleRequest
         * @description 실제 서버 요청 및 UI 업데이트를 수행하는 내부 함수입니다.
         */
        const executeToggleRequest = (): void => {
            const method = currentStatus ? 'DELETE' : 'POST';

            $btn.prop('disabled', true);

            $.ajax({
                url: `/api/v1/follows/${artistId}`,
                type: method,
                success: () => {
                    const newStatus = !currentStatus;
                    $btn.data('status', newStatus);

                    if (newStatus) {
                        $btn.removeClass('btn-follow').addClass('btn-unfollow');
                        $btn.text('취소');
                        Show.success('다시 팔로우했습니다.');
                    } else {
                        $btn.removeClass('btn-unfollow').addClass('btn-follow');
                        $btn.text('팔로우');
                        Show.info('팔로우를 취소했습니다.');
                    }

                    // 내비게이션 바 등 전역 UI 갱신을 위한 커스텀 이벤트를 발생시킵니다.
                    $(document).trigger('artist:nav-update');
                },
                error: (xhr: JQuery.jqXHR) => {
                    const res = xhr.responseJSON as ApiResponse;
                    const msg = res?.error?.message || '요청 처리에 실패했습니다.';
                    Show.error(msg);
                },
                complete: () => {
                    $btn.prop('disabled', false);
                }
            });
        };

        if (currentStatus) {
            Confirm.open({
                title: '팔로우 취소',
                desc: `'${artistName}'님을 팔로우 취소하시겠습니까?`,
                actionText: '취소하기',
                onConfirm: executeToggleRequest
            });
        } else {
            executeToggleRequest();
        }
    });
});