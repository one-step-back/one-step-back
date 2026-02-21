/**
 * @file list.ts
 * @package artist/funding
 * @description 아티스트의 크라우드 펀딩 목록을 서버로부터 조회하고, 화면에 렌더링하는 모듈입니다.
 */

import $ from 'jquery';
import { ApiResponse } from '@/types/api';

/**
 * @interface FundingItem
 * @description 서버로부터 수신되는 개별 펀딩 프로젝트의 데이터 구조입니다.
 */
interface FundingItem {
    id: number;
    title: string;
    mainImgPath: string;
    targetAmount: number;
    currentAmount: number;
    achieveRate?: number;
    startDate: string | number[];
    endDate: string | number[];
    status: string;
    daysLeft?: number;
}

$(() => {
    // ===================================================================================
    //  SECTION: Initialization & Constants
    // ===================================================================================

    const $grid = $('#funding-grid');
    let artistId = $('.funding-container').data('artist-id') as string | number;

    // URL Fallback (data 속성이 실패할 경우 경로에서 추출)
    if (!artistId) {
        const pathSegments = window.location.pathname.split('/');
        const artistIndex = pathSegments.indexOf('artist');
        if (artistIndex > -1 && pathSegments.length > artistIndex + 1) {
            artistId = pathSegments[artistIndex + 1];
        }
    }

    if (!artistId || artistId === 'undefined') {
        console.error("Artist ID not found!");
        $grid.html('<p style="text-align:center; padding: 40px;">아티스트 정보를 찾을 수 없습니다.</p>');
        return;
    }

    // ===================================================================================
    //  SECTION: Helper Functions
    // ===================================================================================

    /**
     * @function parseDate
     * @description Spring의 날짜 데이터(배열 또는 문자열)를 안전한 Date 객체로 파싱합니다.
     * @param {string | number[]} dateData - 파싱할 날짜 데이터
     * @returns {Date} 자바스크립트 내장 Date 객체
     */
    const parseDate = (dateData: string | number[]): Date => {
        if (Array.isArray(dateData)) {
            return new Date(dateData[0], dateData[1] - 1, dateData[2], dateData[3] || 0, dateData[4] || 0, dateData[5] || 0);
        }
        return new Date(dateData);
    };

    /**
     * @function formatWon
     * @description 금액 데이터를 한국 원화 단위 포맷으로 변환합니다.
     */
    const formatWon = (val: number): string => {
        return (val || 0).toLocaleString() + '원';
    };

    /**
     * @function getStatusLabel
     * @description 영문 상태 코드를 사용자 친화적인 한글 라벨로 변환합니다.
     */
    const getStatusLabel = (status: string): string => {
        switch (status) {
            case 'UPCOMING': return '오픈 예정';
            case 'WAITING': return '승인 대기';
            case 'PROCEEDING': return '진행 중';
            case 'SUCCESS': return '펀딩 성공';
            case 'FAILED': return '펀딩 실패';
            case 'ENDED': return '종료됨';
            case 'REJECTED': return '반려됨';
            case 'CANCELLED': return '취소됨';
            case 'COMPLETED': return '완료됨';
            default: return status;
        }
    };

    // ===================================================================================
    //  SECTION: Core Process Functions
    // ===================================================================================

    /**
     * @function loadFundingList
     * @description 백엔드 API를 호출하여 아티스트의 펀딩 목록 데이터를 조회합니다.
     */
    const loadFundingList = (): void => {
        $.ajax({
            url: `/api/v1/crowd-funding/list?artistId=${artistId}`,
            type: 'GET',
            success: (response: ApiResponse<FundingItem[]>) => {
                const list = response.data || [];
                renderFundingList(list);
            },
            error: (xhr: JQuery.jqXHR) => {
                console.error("펀딩 목록 로드 실패", xhr);
                $grid.html('<p style="text-align:center; padding: 40px;">데이터를 불러오지 못했습니다.</p>');
            }
        });
    };

    /**
     * @function renderFundingList
     * @description 수신된 펀딩 데이터를 기반으로 카드를 생성하고 화면에 렌더링합니다.
     */
    const renderFundingList = (list: FundingItem[]): void => {
        let html = '';

        if (!list || list.length === 0) {
            html = `
                <div class="empty-funding">
                    <i class="fas fa-hand-holding-heart"></i>
                    <p>진행 중인 펀딩이 없습니다.</p>
                </div>
            `;
            $grid.html(html);
            return;
        }

        list.forEach(item => {
            const now = new Date();
            const startDate = parseDate(item.startDate);

            let displayStatus = item.status;
            let isUpcoming = false;

            if (item.status === 'PROCEEDING' && startDate.getTime() > now.getTime()) {
                displayStatus = 'UPCOMING';
                isUpcoming = true;
            }

            const statusLabel = getStatusLabel(displayStatus);
            const percent = item.achieveRate !== undefined
                ? item.achieveRate
                : Math.floor((item.currentAmount / item.targetAmount) * 100);

            let dayText: string;

            if (isUpcoming) {
                const diffTime = startDate.getTime() - now.getTime();
                const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
                dayText = `<span class="days-left" style="color:#7c3aed; font-weight:700;">D-${diffDays} (오픈)</span>`;
            }
            else if (displayStatus === 'PROCEEDING') {
                if (item.daysLeft !== undefined) {
                    dayText = `<span class="days-left" style="color:#2563eb; font-weight:700;">${item.daysLeft}일 남음</span>`;
                } else {
                    dayText = `<span class="days-left">진행 중</span>`;
                }
            }
            else if (displayStatus === 'WAITING') {
                dayText = `<span class="days-left" style="color:#f59e0b;">승인 대기</span>`;
            }
            else {
                dayText = `<span class="days-left" style="color:#64748b;">종료됨</span>`;
            }

            html += `
                <a href="/artist/${artistId}/funding/${item.id}" class="funding-card">
                    <div class="card-thumb">
                        <img src="${item.mainImgPath}" alt="${item.title}">
                        <span class="status-label status-${item.status}">${statusLabel}</span>
                    </div>
                    <div class="card-body">
                        <h3 class="card-title">${item.title}</h3>
                        <div class="card-meta">
                            목표 ${formatWon(item.targetAmount)}
                        </div>
                        <div class="card-progress-area">
                            <div class="progress-info">
                                <span class="percent">${percent}%</span>
                                <span class="amount">${formatWon(item.currentAmount)}</span>
                            </div>
                            <div class="progress-bar">
                                <div class="progress-fill" style="width: ${Math.min(percent, 100)}%"></div>
                            </div>
                            <div class="card-days">
                                ${dayText}
                            </div>
                        </div>
                    </div>
                </a>
            `;
        });

        $grid.html(html);
    };

    // Initialize System
    loadFundingList();
});