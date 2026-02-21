/**
 * @file main.ts
 * @package artist
 * @description 아티스트 페이지의 핵심 기능(팔로우, 공유, 프로필 팝오버)을 제어하는 모듈입니다.
 * 1. 팔로우 시스템: 낙관적 UI(Optimistic UI) 업데이트, REST API 연동, 네비게이션 동기화 이벤트 트리거
 * 2. 공유 시스템: 현재 페이지 URL 클립보드 복사 및 모달 제어
 * 3. 프로필 팝오버: 아티스트 상세 정보 비동기 로드 및 UI 렌더링
 */

import $ from 'jquery';
import {Show} from '@/util/toast';
import {ApiResponse} from '@/types/api';

/**
 * @interface MemberInfo
 * @description 서버로부터 수신되는 아티스트/회원 상세 정보의 데이터 구조입니다.
 */
interface MemberInfo {
    email: string;
    nickname: string;
    description?: string;
}

$(() => {
    // ===================================================================================
    //  SECTION: Constants & Configuration
    // ===================================================================================

    /** @constant {string | undefined} CURRENT_MEMBER_ID - 현재 로그인한 사용자의 식별자입니다. */
    const CURRENT_MEMBER_ID = $('body').data('member-id') as string | undefined;

    /** @constant {Object} API_URLS - REST API 통신을 위한 엔드포인트 목록입니다. */
    const API_URLS = {
        FOLLOW: '/api/v1/follows/',
        MEMBER_INFO: '/api/v1/members/info/'
    };

    /** @constant {Object} PATHS - 클라이언트 라우팅 경로 목록입니다. */
    const PATHS = {
        LOGIN: '/member/login-email'
    };

    /** @constant {Object} CONFIG - 애니메이션 및 타임아웃 관련 환경 설정값입니다. */
    const CONFIG = {
        CONFETTI_DURATION: 800,
        REDIRECT_DELAY: 1000
    };

    // ===================================================================================
    //  SECTION: DOM Elements
    // ===================================================================================

    const $followBtn = $('#btn-follow');
    const $followerCount = $('#artist-follower-count');

    const $shareModal = $('#share-modal');
    const $shareBtn = $('#btn-share');
    const $closeBtn = $('.artist-modal__close-btn, .artist-modal__backdrop');
    const $urlInput = $('.artist-input-group__field');
    const $copyBtn = $('.artist-input-group__btn');

    const $profileAvatar = $('#btn-profile-info');
    const $infoCard = $('#profile-info-card');

    // ===================================================================================
    //  SECTION: Follow System
    // ===================================================================================

    /**
     * @function initFollowSystem
     * @description 팔로우 버튼의 클릭 이벤트 리스너를 바인딩하여 시스템을 초기화합니다.
     */
    function initFollowSystem(): void {
        if ($followBtn.length) {
            $followBtn.on('click', handleFollowClick);
        }
    }

    /**
     * @function handleFollowClick
     * @description 팔로우 버튼 클릭 시 인가 여부를 검증하고, 낙관적 업데이트를 수행한 뒤 API를 호출합니다.
     * @param {JQuery.ClickEvent} e - 제이쿼리 클릭 이벤트 객체
     */
    function handleFollowClick(e: JQuery.ClickEvent): void {
        const $btn = $(e.currentTarget);
        const artistId = $btn.data('id') as number;
        const currentStatus = $btn.data('status') as boolean;

        // 논리적 오류 수정: artistId가 아닌 현재 로그인 세션(CURRENT_MEMBER_ID)을 기준으로 권한을 검증합니다.
        if (!CURRENT_MEMBER_ID) {
            Show.error('로그인이 필요한 서비스입니다.');
            setTimeout(() => location.href = PATHS.LOGIN, CONFIG.REDIRECT_DELAY);
            return;
        }

        const targetStatus = !currentStatus;
        const currentCountText = $followerCount.text().replace(/,/g, '');
        const currentCount = parseInt(currentCountText, 10) || 0;

        // Optimistic UI Update 적용
        if (targetStatus) {
            updateFollowButtonState(true);
            triggerConfettiEffect($btn);
            $followerCount.text((currentCount + 1).toLocaleString());
            Show.success('아티스트를 팔로우합니다!');
        } else {
            updateFollowButtonState(false);
            if (currentCount > 0) {
                $followerCount.text((currentCount - 1).toLocaleString());
            }
            Show.info('팔로우를 취소했습니다.');
        }

        const method = targetStatus ? 'POST' : 'DELETE';

        $.ajax({
            url: API_URLS.FOLLOW + artistId,
            type: method,
            success: () => {
                if (CURRENT_MEMBER_ID) {
                    $(document).trigger('artist:nav-update');
                }
            },
            error: (xhr: JQuery.jqXHR) => {
                handleFollowError(xhr, targetStatus, currentCount);
            }
        });
    }

    /**
     * @function handleFollowError
     * @description 서버 API 호출 실패 시 에러 메시지를 출력하고 UI 상태를 원래대로 롤백합니다.
     * @param {JQuery.jqXHR} xhr - 서버 응답 객체
     * @param {boolean} targetStatus - 실패한 목표 팔로우 상태
     * @param {number} originalCount - 롤백할 기존 팔로워 수
     */
    function handleFollowError(xhr: JQuery.jqXHR, targetStatus: boolean, originalCount: number): void {
        console.error("[Follow] Request failed:", xhr);

        if (xhr.status === 401) {
            Show.error('로그인이 필요합니다.');
            location.href = PATHS.LOGIN;
        } else {
            const res = xhr.responseJSON as ApiResponse;
            const msg = res?.error?.message || '오류가 발생했습니다. 다시 시도해주세요.';
            Show.error(msg);
        }

        // UI 롤백 수행
        updateFollowButtonState(!targetStatus);
        $followerCount.text(originalCount.toLocaleString());
    }

    /**
     * @function updateFollowButtonState
     * @description 팔로우 버튼의 상태(활성화/비활성화)에 따라 CSS 클래스 및 텍스트를 변경합니다.
     * @param {boolean} isActive - 활성화 적용 여부
     */
    function updateFollowButtonState(isActive: boolean): void {
        $followBtn.data('status', isActive);

        if (isActive) {
            $followBtn.removeClass('artist-btn--primary').addClass('artist-btn--active');
            $followBtn.find('span').text('팔로잉');
        } else {
            $followBtn.removeClass('artist-btn--active').addClass('artist-btn--primary');
            $followBtn.find('span').text('팔로우');
        }
    }

    /**
     * @function triggerConfettiEffect
     * @description 대상 요소에 일시적으로 애니메이션 클래스를 부여합니다.
     * @param {JQuery} $element - 애니메이션을 적용할 제이쿼리 객체
     */
    function triggerConfettiEffect($element: JQuery): void {
        $element.addClass('artist-btn--animate');
        setTimeout(() => {
            $element.removeClass('artist-btn--animate');
        }, CONFIG.CONFETTI_DURATION);
    }

    // ===================================================================================
    //  SECTION: Share System
    // ===================================================================================

    /**
     * @function initShareSystem
     * @description 공유 모달 및 링크 복사 기능을 위한 이벤트를 바인딩합니다.
     */
    function initShareSystem(): void {
        $shareBtn.on('click', openShareModal);
        $closeBtn.on('click', closeShareModal);
        $copyBtn.on('click', copyUrlToClipboard);
    }

    /**
     * @function openShareModal
     * @description 공유 모달을 활성화하고 현재 브라우저의 URL을 입력 필드에 주입합니다.
     */
    function openShareModal(): void {
        $urlInput.val(window.location.href);
        $shareModal.addClass('artist-modal--open');
    }

    /**
     * @function closeShareModal
     * @description 공유 모달을 비활성화하여 화면에서 숨깁니다.
     */
    function closeShareModal(): void {
        $shareModal.removeClass('artist-modal--open');
    }

    /**
     * @function copyUrlToClipboard
     * @description 입력 필드의 URL 텍스트를 시스템 클립보드에 복사합니다.
     */
    function copyUrlToClipboard(): void {
        $urlInput.trigger('select');
        try {
            document.execCommand('copy');
            Show.success('주소가 복사되었습니다.');
        } catch (err) {
            Show.error('복사에 실패했습니다.');
        }
        closeShareModal();
    }

    // ===================================================================================
    //  SECTION: Profile Info Popover
    // ===================================================================================

    let isProfileInfoLoaded = false;
    const ACTIVE_CLASS = 'artist-profile__info-card--active';

    /**
     * @function initProfilePopover
     * @description 프로필 아바타 클릭 및 외부 영역 클릭에 대한 팝오버 이벤트를 초기화합니다.
     */
    function initProfilePopover(): void {
        $(document).on('click', (e: JQuery.ClickEvent) => {
            if (!$(e.target as HTMLElement).closest('.artist-profile__avatar-area').length) {
                $infoCard.removeClass(ACTIVE_CLASS);
            }
        });

        $profileAvatar.on('click', handleProfileClick);
    }

    /**
     * @function handleProfileClick
     * @description 프로필 아바타 클릭 시 정보를 비동기로 로드하거나 기존 캐시 데이터를 기반으로 토글합니다.
     * @param {JQuery.ClickEvent} e - 제이쿼리 클릭 이벤트 객체
     */
    function handleProfileClick(e: JQuery.ClickEvent): void {
        e.stopPropagation();
        const $target = $(e.currentTarget);
        const memberId = $target.data('id') as number;

        if ($infoCard.hasClass(ACTIVE_CLASS)) {
            $infoCard.removeClass(ACTIVE_CLASS);
            return;
        }

        if (isProfileInfoLoaded) {
            $infoCard.addClass(ACTIVE_CLASS);
            return;
        }

        fetchMemberInfo(memberId);
    }

    /**
     * @function fetchMemberInfo
     * @description 서버 API를 통해 아티스트의 세부 정보를 조회합니다.
     * @param {number} memberId - 조회 대상 아티스트의 고유 식별자
     */
    function fetchMemberInfo(memberId: number): void {
        $.ajax({
            url: API_URLS.MEMBER_INFO + memberId,
            type: 'GET',
            success: (response: ApiResponse<MemberInfo>) => {
                if (response.success && response.data) {
                    renderProfileInfo(response.data);
                } else {
                    Show.error("정보를 불러오지 못했습니다.");
                }
            },
            error: () => {
                Show.error("서버 통신 오류가 발생했습니다.");
            }
        });
    }

    /**
     * @function renderProfileInfo
     * @description API를 통해 수신한 정보를 HTML 구조로 매핑하여 팝오버 영역에 렌더링합니다.
     * @param {MemberInfo} info - 렌더링에 사용될 회원 정보 객체
     */
    function renderProfileInfo(info: MemberInfo): void {
        const html = `
            <div class="info-card__row">
                <span class="info-card__label">이메일</span>
                <div class="info-card__value">${info.email}</div>
            </div>
            <div class="info-card__row">
                <span class="info-card__label">닉네임</span>
                <div class="info-card__value">${info.nickname}</div>
            </div>
            <div class="info-card__row">
                <span class="info-card__label">소개</span>
                <div class="info-card__value" style="font-size: 14px; color: #666;">
                    ${info.description ? info.description : '소개글이 없습니다.'}
                </div>
            </div>
        `;
        $infoCard.html(html);
        $infoCard.addClass(ACTIVE_CLASS);
        isProfileInfoLoaded = true;
    }

    // ===================================================================================
    //  SECTION: Bootstrap
    // ===================================================================================

    initFollowSystem();
    initShareSystem();
    initProfilePopover();
});