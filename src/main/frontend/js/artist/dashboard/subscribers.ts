/**
 * @file subscribers.ts
 * @package artist/dashboard
 * @description 아티스트 대시보드 - 구독자(멤버십) 목록을 서버로부터 조회하고 테이블에 렌더링하는 모듈입니다.
 */

import $ from 'jquery';
import {ApiResponse} from '@/types/api';

/**
 * @interface SubscriberItem
 * @description 서버로부터 수신되는 개별 구독자의 데이터 구조입니다.
 */
interface SubscriberItem {
    profilePath?: string;
    nickname: string;
    membershipName: string;
    levelOrder: number;
    startDate: string | number[];
}

$(() => {
    // ===================================================================================
    //  SECTION: DOM Elements & State
    // ===================================================================================

    const $tbody = $('#subscriber-list-body');
    const $emptyState = $('#subscriber-empty');
    const $countDisplay = $('#total-subscriber-count');

    // ===================================================================================
    //  SECTION: Helper Functions
    // ===================================================================================

    /**
     * @function parseDate
     * @description Spring의 날짜 데이터(배열 또는 문자열)를 'YYYY.MM.DD' 포맷으로 파싱합니다.
     */
    const parseDate = (dateData: string | number[]): string => {
        if (!dateData) return '-';
        if (Array.isArray(dateData)) {
            return `${dateData[0]}.${String(dateData[1]).padStart(2, '0')}.${String(dateData[2]).padStart(2, '0')}`;
        }
        return dateData.split('T')[0].replace(/-/g, '.');
    };

    /**
     * @function renderTable
     * @description 수신된 구독자 데이터를 기반으로 테이블 행(Row)을 렌더링합니다.
     */
    const renderTable = (list: SubscriberItem[]): void => {
        $tbody.empty();

        if (!list || list.length === 0) {
            $emptyState.show();
            if ($countDisplay.length) $countDisplay.text('0');
            return;
        }

        $emptyState.hide();
        if ($countDisplay.length) $countDisplay.text(list.length.toString());

        let html = '';
        list.forEach(sub => {
            const startDate = parseDate(sub.startDate);
            const profileSrc = sub.profilePath || '/images/default-profile.png';

            html += `
                <tr>
                    <td>
                        <div class="profile-cell">
                            <img src="${profileSrc}" alt="프로필" class="profile-img">
                            <span class="nickname">${sub.nickname}</span>
                        </div>
                    </td>
                    <td>
                        <div class="membership-info">
                            <span class="membership-name">${sub.membershipName}</span>
                            <span class="tier-badge">Lv.${sub.levelOrder}</span>
                        </div>
                    </td>
                    <td>
                        <span class="date-text">${startDate}</span>
                    </td>
                    <td>
                        <span class="status-badge">구독 중</span>
                    </td>
                </tr>
            `;
        });

        $tbody.html(html);
    };

    // ===================================================================================
    //  SECTION: Core Process
    // ===================================================================================

    /**
     * @function loadSubscribers
     * @description 서버 API를 호출하여 구독자 목록을 조회합니다.
     */
    const loadSubscribers = (): void => {
        $.ajax({
            url: '/api/v1/subscription/artist/subscribers',
            type: 'GET',
            success: (response: ApiResponse<SubscriberItem[]>) => {
                const list = response.data || [];
                renderTable(list);
            },
            error: (xhr: JQuery.jqXHR) => {
                console.error('[Error] 구독자 로드 실패:', xhr);
                $emptyState.show();
            }
        });
    };

    // Initialize System
    loadSubscribers();
});