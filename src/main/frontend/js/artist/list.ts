/**
 * @file list.ts
 * @description 아티스트 디스커버리 페이지 로직 (3D Flip 팔로우 버튼 적용)
 */

import $ from "jquery";
import {Show} from '@/util/toast';

$(() => {
    /** @type {string|undefined} 현재 로그인한 사용자의 ID */
    const CURRENT_MEMBER_ID: string | undefined = $('body').data('member-id')?.toString();

    const API_URLS = {FOLLOW: '/api/v1/follows/'};
    const PATHS = {LOGIN: '/member/login-email'};

    /**
     * 아티스트 그리드 내 팔로우 버튼 클릭 이벤트 바인딩
     * 부모 요소(.artist-list__grid)에 위임하여 동적 로딩 대응
     */
    $('.artist-list__grid').on('click', '.artist-card__follow-btn', (e: JQuery.ClickEvent) => {
        e.preventDefault();
        e.stopPropagation();

        const $btn = $(e.currentTarget);
        const artistId = $btn.data('id');
        const currentStatus: boolean = $btn.data('status'); // 현재 팔로우 상태

        // 1. 권한 체크 (로그인 여부)
        if (!CURRENT_MEMBER_ID) {
            Show.error('로그인이 필요한 서비스입니다.');
            setTimeout(() => location.href = PATHS.LOGIN, 1000);
            return;
        }

        const targetStatus = !currentStatus;
        const method = targetStatus ? 'POST' : 'DELETE';

        // 2. UI 즉시 업데이트 (Optimistic UI)
        // 서버 응답 전 클래스를 토글하여 3D Flip 애니메이션(180도 회전) 수행
        toggleBtnState($btn, targetStatus);

        // 3. 서버 데이터 동기화
        $.ajax({
            url: API_URLS.FOLLOW + artistId,
            type: method,
            success: () => {
                // 내비게이션 바 갱신을 위한 전역 신호 발송
                $(document).trigger('artist:nav-update');
            },
            error: (xhr) => {
                console.error("Follow request failed:", xhr);
                // 요청 실패 시 상태 롤백
                toggleBtnState($btn, !targetStatus);
                Show.error('요청을 처리할 수 없습니다.');
            }
        });
    });

    /**
     * 팔로우 버튼의 시각적 상태 및 메타데이터를 토글합니다.
     * @param {JQuery} $btn 토글할 버튼 객체
     * @param {boolean} isActive 활성화(팔로우) 여부
     */
    function toggleBtnState($btn: JQuery, isActive: boolean): void {
        $btn.data('status', isActive);

        if (isActive) {
            // CSS에서 transform: rotateY(180deg) 처리를 위해 클래스 추가
            $btn.addClass('artist-card__follow-btn--active');
            $btn.attr('title', '팔로우 취소');
        } else {
            // 기본 상태로 복구
            $btn.removeClass('artist-card__follow-btn--active');
            $btn.attr('title', '팔로우');
        }
    }
});