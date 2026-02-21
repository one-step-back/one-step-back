/**
 * @file followers.ts
 * @package artist/dashboard
 * @description 아티스트 대시보드 - 팔로워 목록을 서버로부터 조회하고 테이블에 렌더링하는 모듈입니다.
 */

import $ from 'jquery';
import {ApiResponse} from '@/types/api';

/**
 * @interface FollowerItem
 * @description 서버로부터 수신되는 개별 팔로워의 데이터 구조입니다.
 */
interface FollowerItem {
    artistProfilePath?: string;
    artistNickname: string;
    followTime: string | number[];
}

$(() => {
    // ===================================================================================
    //  SECTION: DOM Elements & State
    // ===================================================================================

    const $tbody = $('#follower-list-body');
    const $emptyState = $('#follower-empty');
    const $countDisplay = $('#total-follower-count');

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
     * @description 수신된 팔로워 데이터를 기반으로 테이블 행(Row)을 렌더링합니다.
     */
    const renderTable = (list: FollowerItem[]): void => {
        $tbody.empty();

        if (!list || list.length === 0) {
            $emptyState.show();
            if ($countDisplay.length) $countDisplay.text('0');
            return;
        }

        $emptyState.hide();
        if ($countDisplay.length) $countDisplay.text(list.length.toString());

        let html = '';
        list.forEach(item => {
            const date = parseDate(item.followTime);
            const profileSrc = item.artistProfilePath || '/images/default-profile.png';
            const nickname = item.artistNickname;

            html += `
                <tr>
                    <td>
                        <div class="profile-cell">
                            <img src="${profileSrc}" alt="프로필" class="profile-img">
                            <span class="nickname">${nickname}</span>
                        </div>
                    </td>
                    <td>
                        <span class="date-text">${date}</span>
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
     * @function loadFollowers
     * @description 서버 API를 호출하여 팔로워 목록을 조회합니다.
     */
    const loadFollowers = (): void => {
        $.ajax({
            url: '/api/v1/follows/artist/followers',
            type: 'GET',
            success: (response: ApiResponse<FollowerItem[]>) => {
                const list = response.data || [];
                renderTable(list);
            },
            error: (xhr: JQuery.jqXHR) => {
                console.error('[Error] 팔로워 로드 실패:', xhr);
                $emptyState.show();
            }
        });
    };

    // Initialize System
    loadFollowers();
});