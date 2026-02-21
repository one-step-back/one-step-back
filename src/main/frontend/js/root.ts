/**
 * @file root.ts
 * @description 애플리케이션 전역 레이아웃 및 공통 모듈 (사이드바, 알림, 유틸리티)
 */

import $ from 'jquery';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

/** 아티스트 아이콘 렌더링을 위한 기본 인터페이스 */
interface ArtistIcon {
    id: number;
    nickname: string;
    blogName: string;
    profilePath: string;
}

/** 연결된 아티스트 API 응답 구조 */
interface ConnectedArtists {
    subscriptions: ArtistIcon[];
    follows: ArtistIcon[];
}

/** 시스템 알림 객체 구조 */
interface NotificationItem {
    id: number;
    notiMessage: string;
    notiUrl: string;
    timestamp?: number;
}

/** 로컬 스토리지 알림 캐시 구조 */
interface NotiStorageData {
    timestamp: number;
    list: NotificationItem[];
}

$(() => {
    /** @type {string|undefined} body 태그에 주입된 현재 세션 사용자 ID */
    const CURRENT_MEMBER_ID: string | undefined = $('body').data('member-id')?.toString();

    const STORAGE_KEYS = {
        NAV_CACHE: `artist_nav_cache_${CURRENT_MEMBER_ID}`,
        NOTIFICATION: `noti_${CURRENT_MEMBER_ID}`
    };

    const API_URLS = {
        NAV_CONNECTED: '/api/v1/artists/connected',
        NOTI_UNREAD: '/api/v1/notifications/unread',
        NOTI_READ: '/api/v1/notifications/'
    };

    const CONFIG = {
        NAV_MIN_LOADING_TIME: 500, // 최소 로딩 시간 (스켈레톤 UI 깜빡임 방지)
        NOTI_CACHE_EXPIRATION: 30 * 60 * 1000, // 알림 캐시 유효 시간 (30분)
        SKELETON_COUNT: 5
    };

    const $sideNav = $('.side-nav');
    const $main = $('main');
    const $menuToggle = $('#menu-toggle');
    const $headerNav = $('#header-nav');
    const $menuIcon = $menuToggle.find('i');
    const $userToggle = $('#user-menu-toggle');
    const $userDropdown = $('#user-dropdown');
    const $notiBadge = $('#notification-badge');
    const $notiList = $('#notification-list');

    // ===================================================================================
    //  사이드 내비게이션 (Navigation)
    //  - 세션 스토리지를 활용한 캐싱 전략 적용
    // ===================================================================================

    /** 내비게이션 초기화 및 아티스트 목록 로드 */
    function initNavigation(): void {
        if ($sideNav.length > 0) {
            $(document).on('artist:nav-update', handleNavUpdateSignal);
            loadConnectedArtists();
        } else {
            $main.css('justify-content', 'center'); // 사이드바 없을 시 메인 중앙 정렬
        }
    }

    /** 외부 모듈에서 보낸 내비게이션 갱신 신호 처리 */
    function handleNavUpdateSignal(): void {
        sessionStorage.removeItem(STORAGE_KEYS.NAV_CACHE);
        loadConnectedArtists();
    }

    /** 캐시 또는 API를 통해 연결된 아티스트(구독/팔로우) 로드 */
    function loadConnectedArtists(): void {
        const cachedData = sessionStorage.getItem(STORAGE_KEYS.NAV_CACHE);
        if (cachedData) {
            try {
                renderSideNav(JSON.parse(cachedData));
                return;
            } catch (e) {
                sessionStorage.removeItem(STORAGE_KEYS.NAV_CACHE);
            }
        }

        renderSkeleton();
        const startTime = Date.now();

        $.ajax({
            url: API_URLS.NAV_CONNECTED,
            type: 'GET',
            success: (res: any) => handleNavResponse(res, startTime),
            error: () => handleNavError(startTime)
        });
    }

    /** API 응답 처리 및 지연 렌더링으로 UX 최적화 */
    function handleNavResponse(response: any, startTime: number): void {
        const remainingTime = Math.max(0, CONFIG.NAV_MIN_LOADING_TIME - (Date.now() - startTime));
        setTimeout(() => {
            if (response.success) {
                sessionStorage.setItem(STORAGE_KEYS.NAV_CACHE, JSON.stringify(response.data));
                renderSideNav(response.data);
            }
        }, remainingTime);
    }

    /** 로드 실패 시 빈 목록 렌더링 */
    function handleNavError(startTime: number): void {
        const remainingTime = Math.max(0, CONFIG.NAV_MIN_LOADING_TIME - (Date.now() - startTime));
        setTimeout(() => renderSideNav({subscriptions: [], follows: []}), remainingTime);
    }

    /** 로딩 상태를 보여주기 위한 스켈레톤 UI 생성 */
    function renderSkeleton(): void {
        let html = '<div class="side-nav__section">';
        for (let i = 0; i < CONFIG.SKELETON_COUNT; i++) {
            html += '<div class="side-nav__skeleton"></div>';
        }
        html += '</div>';
        $sideNav.html(html);
    }

    /** 아티스트 데이터를 기반으로 사이드바 HTML 렌더링 */
    function renderSideNav(data: ConnectedArtists): void {
        let html = '';
        const subIds = new Set(data.subscriptions.map(a => a.id));
        const uniqueFollows = data.follows.filter(a => !subIds.has(a.id));

        const hasSubs = data.subscriptions.length > 0;
        const hasFollows = uniqueFollows.length > 0;

        // 1. 유료 구독 아티스트 섹션
        if (hasSubs) {
            html += '<div class="side-nav__section">';
            data.subscriptions.forEach(a => html += createArtistIconHtml(a));
            html += '</div>';
        }

        if (hasSubs && hasFollows) html += '<div class="side-nav__divider"></div>';

        // 2. 무료 팔로우 아티스트 섹션
        if (hasFollows) {
            html += '<div class="side-nav__section">';
            uniqueFollows.forEach(a => html += createArtistIconHtml(a));
            html += '</div>';
        }

        if (hasSubs || hasFollows) html += '<div class="side-nav__divider"></div>';

        // 3. 아티스트 찾기 추가 버튼
        html += `
            <div class="side-nav__section">
                <a href="/artist/list" class="artist-icon artist-icon--empty" data-name="아티스트 찾기" data-blog="새로운 아티스트를 찾아보세요!">
                    <i class="fa-solid fa-plus"></i>
                </a>
            </div>
        `;

        $sideNav.html(html).hide().fadeIn(300);
    }

    /** 개별 아티스트 아이콘 HTML 생성 */
    function createArtistIconHtml(artist: ArtistIcon): string {
        return `
            <a href="/artist/${artist.id}" class="artist-icon" data-name="${artist.nickname}" data-blog="${artist.blogName}">
                <img src="${artist.profilePath}" alt="${artist.nickname}">
            </a>
        `;
    }

    // ===================================================================================
    //  공통 UI 컴포넌트 (Tooltip, Header)
    // ===================================================================================

    const $globalTooltip = $('<div id="global-tooltip"></div>').appendTo('body');

    /**
     * 사이드바 아티스트 아이콘 호버 시 정보 툴팁 노출 로직
     */
    $(document).on('mouseenter', '.artist-icon', (e: JQuery.MouseEnterEvent) => {
        // this 대신 currentTarget을 사용하여 타입을 명확히 정의한다
        const target = e.currentTarget as HTMLElement;
        const $this = $(target);

        const name = $this.data('name');
        const blog = $this.data('blog');

        $globalTooltip.html(`
        <div class="tooltip-blog">${blog || name + "'s World"}</div>
        <div class="tooltip-name">${name}</div>
    `);

        const rect = target.getBoundingClientRect();

        $globalTooltip.css({
            top: (rect.top + rect.height / 2 - ($globalTooltip.outerHeight() || 0) / 2) + 'px',
            left: (rect.right + 12) + 'px',
            opacity: 1,
            visibility: 'visible'
        });
    });

    $(document).on('mouseleave', '.artist-icon', () => $globalTooltip.css({opacity: 0, visibility: 'hidden'}));

    /** 헤더 모바일 메뉴 토글 */
    if ($menuToggle.length) {
        $menuToggle.on('click', () => {
            $headerNav.toggleClass('header__nav--open');
            $menuIcon.toggleClass('fa-bars fa-times');
        });
    }

    /** 유저 프로필 드롭다운 제어 */
    if ($userToggle.length && $userDropdown.length) {
        $userToggle.on('click', e => {
            e.stopPropagation();
            $userDropdown.toggleClass('header__user-dropdown--active');
        });

        $(document).on('click', e => {
            if (!$userDropdown.is(e.target as any) && $userDropdown.has(e.target as any).length === 0 &&
                !$userToggle.is(e.target as any) && $userToggle.has(e.target as any).length === 0) {
                $userDropdown.removeClass('header__user-dropdown--active');
            }
        });
    }

    // ===================================================================================
    //  실시간 알림 시스템 (WebSocket & Storage)
    // ===================================================================================

    /** 알림 모듈 초기화 */
    function initNotification(): void {
        if (CURRENT_MEMBER_ID) {
            loadNotifications();
            connectWebSocket(CURRENT_MEMBER_ID);
        }
    }

    /** 저장된 알림 로드 및 캐시 만료 시 API 호출 */
    function loadNotifications(): void {
        const cachedRaw = localStorage.getItem(STORAGE_KEYS.NOTIFICATION);
        if (cachedRaw) {
            try {
                const cachedData: NotiStorageData = JSON.parse(cachedRaw);
                if (Date.now() - cachedData.timestamp > CONFIG.NOTI_CACHE_EXPIRATION) {
                    fetchNotificationsFromApi();
                } else {
                    renderNotificationList(cachedData.list);
                }
            } catch (e) {
                fetchNotificationsFromApi();
            }
        } else {
            fetchNotificationsFromApi();
        }
    }

    /** 읽지 않은 알림 목록 API 요청 */
    function fetchNotificationsFromApi(): void {
        $.ajax({
            url: API_URLS.NOTI_UNREAD,
            type: 'GET',
            success: (data: NotificationItem[]) => {
                saveNotificationsToStorage(data || []);
                renderNotificationList(data || []);
            }
        });
    }

    /** 알림 리스트 렌더링 및 배지 카운트 갱신 */
    function renderNotificationList(notifications: NotificationItem[]): void {
        if (notifications && notifications.length > 0) {
            $notiBadge.text(notifications.length).show();
            let html = '';
            notifications.forEach(n => html += makeNotificationHtml(n));
            $notiList.html(html);
        } else {
            $notiBadge.hide();
            $notiList.html('<li class="empty-message">새로운 알림이 없습니다.</li>');
        }
    }

    /** WebSocket(Stomp)을 통한 실시간 알림 채널 연결 */
    function connectWebSocket(memberId: string): void {
        if (typeof SockJS === 'undefined' || typeof Stomp === 'undefined') return;
        const stompClient = Stomp.over(new SockJS('/ws-stomp'));
        stompClient.debug = () => {};
        stompClient.connect({}, () => {
            stompClient.subscribe('/sub/notification/' + memberId, (msg: any) => {
                handleRealtimeNotification(JSON.parse(msg.body));
            });
        });
    }

    /** 웹소켓으로 수신된 알림을 기존 목록 상단에 추가 */
    function handleRealtimeNotification(noti: NotificationItem): void {
        let currentList: NotificationItem[] = [];
        try {
            const cachedRaw = localStorage.getItem(STORAGE_KEYS.NOTIFICATION);
            if (cachedRaw) currentList = JSON.parse(cachedRaw).list || [];
        } catch (e) {
        }

        currentList.unshift(noti);
        saveNotificationsToStorage(currentList);
        renderNotificationList(currentList);
    }

    /** 알림 데이터를 타임스탬프와 함께 로컬 스토리지에 동기화 */
    function saveNotificationsToStorage(listData: NotificationItem[]): void {
        localStorage.setItem(STORAGE_KEYS.NOTIFICATION, JSON.stringify({timestamp: Date.now(), list: listData}));
    }

    /** 개별 알림 항목 HTML 생성 */
    function makeNotificationHtml(noti: NotificationItem): string {
        return `
            <li class="notification-item unread" data-id="${noti.id}" onclick="handleNotificationClick('${noti.notiUrl}', '${noti.id}')">
                <div class="notification-message">${noti.notiMessage}</div>
                <div class="notification-time">방금 전</div>
            </li>
        `;
    }

    /** 알림 클릭 시 읽음 처리 API 호출 및 해당 URL로 이동 */
    (window as any).handleNotificationClick = function (url: string, notiId: string): void {
        try {
            const cachedRaw = localStorage.getItem(STORAGE_KEYS.NOTIFICATION);
            if (cachedRaw) {
                const data: NotiStorageData = JSON.parse(cachedRaw);
                data.list = data.list.filter(n => n.id.toString() !== notiId);
                localStorage.setItem(STORAGE_KEYS.NOTIFICATION, JSON.stringify(data));
            }
        } catch (e) {
        }

        void fetch(API_URLS.NOTI_READ + notiId + '/read', {
            method: 'PATCH',
            keepalive: true,
            headers: {'Content-Type': 'application/json'}
        });
        location.href = url;
    };

    initNavigation();
    initNotification();
});