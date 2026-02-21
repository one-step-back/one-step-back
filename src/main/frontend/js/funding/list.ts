/**
 * @file list.ts
 * @package funding
 * @description 글로벌 크라우드 펀딩 프로젝트 목록을 상태별로 조회하고 화면에 렌더링하는 모듈입니다.
 */

import $ from 'jquery';
import { ApiResponse } from '@/types/api';

/**
 * @interface FundingProjectItem
 * @description 서버로부터 수신되는 개별 펀딩 프로젝트 데이터 구조입니다.
 */
interface FundingProjectItem {
    id: number;
    artistId: number;
    title: string;
    artistName: string;
    achieveRate: number;
    collectedAmount: number;
    status: string;
    daysLeft: number;
    thumbnailPath?: string;
}

$(() => {
    // ===================================================================================
    //  SECTION: DOM Elements & State
    // ===================================================================================

    const $grid = $('#funding-list-grid');
    const $msg = $('#funding-status-message');

    // ===================================================================================
    //  SECTION: UI Renderers & Helpers
    // ===================================================================================

    const showError = (text: string): void => {
        $msg.html(`<i class="fas fa-exclamation-circle"></i><br>${text}`).show();
    };

    /**
     * @description 수신된 펀딩 데이터를 기반으로 카드 그리드를 렌더링합니다.
     */
    const renderGrid = (data: FundingProjectItem[]): void => {
        if (!data || data.length === 0) {
            $msg.html('<i class="fas fa-search"></i><br>조건에 맞는 펀딩 프로젝트가 없습니다.').show();
            return;
        }

        $msg.hide();

        let html = '';
        data.forEach(f => {
            let badgeClass = '';
            let badgeText = '';
            let cardClass = 'funding-card';

            switch (f.status) {
                case 'PROCEEDING':
                    badgeClass = 'badge-proceeding';
                    badgeText = f.daysLeft > 0 ? `D-${f.daysLeft}` : (f.daysLeft === 0 ? '오늘 마감' : '마감 임박');
                    break;
                case 'WAITING':
                case 'UPCOMING':
                    badgeClass = 'badge-upcoming';
                    badgeText = '오픈 예정';
                    break;
                case 'SUCCESS':
                    badgeClass = 'badge-success';
                    badgeText = '펀딩 성공';
                    cardClass += ' ended';
                    break;
                case 'ENDED':
                case 'FAILED':
                case 'REJECTED':
                case 'CANCELLED':
                    badgeClass = 'badge-ended';
                    badgeText = '종료됨';
                    cardClass += ' ended';
                    break;
                default:
                    badgeClass = 'badge-ended';
                    badgeText = f.status;
            }

            const formattedAmount = f.collectedAmount ? f.collectedAmount.toLocaleString() : '0';
            const thumbnail = f.thumbnailPath || '/images/no-image.png';
            const progressWidth = f.achieveRate > 100 ? 100 : f.achieveRate;

            html += `
                <a href="/artist/${f.artistId}/funding/${f.id}" class="${cardClass}">
                    <div class="card-thumb-box">
                        <span class="card-badge ${badgeClass}">${badgeText}</span>
                        <img src="${thumbnail}" alt="${f.title}" class="card-thumb" loading="lazy">
                    </div>
                    <div class="card-body">
                        <div class="card-artist">
                            <i class="fas fa-user-circle"></i> ${f.artistName}
                        </div>
                        <h3 class="card-title">${f.title}</h3>
                        
                        <div class="card-metrics">
                            <div class="metric-row">
                                <span class="metric-rate">${f.achieveRate}%</span>
                                <span class="metric-amount">${formattedAmount}원</span>
                            </div>
                            <div class="progress-container">
                                <div class="progress-fill" style="width: ${progressWidth}%"></div>
                            </div>
                        </div>
                    </div>
                </a>
            `;
        });

        $grid.html(html);
    };

    // ===================================================================================
    //  SECTION: Core Process
    // ===================================================================================

    /**
     * @description 지정된 상태값에 맞춰 서버에서 펀딩 프로젝트 목록을 비동기로 로드합니다.
     */
    const loadFundings = (status: string): void => {
        $grid.empty();
        $msg.html('<div class="spinner"><i class="fas fa-circle-notch fa-spin"></i></div>').show();

        $.ajax({
            url: '/api/v1/crowd-funding/list',
            type: 'GET',
            data: { status: status },
            success: (response: ApiResponse<FundingProjectItem[]>) => {
                const list = response.data || [];
                renderGrid(list);
            },
            error: () => {
                showError('서버 통신 중 오류가 발생했습니다.');
            }
        });
    };

    // ===================================================================================
    //  SECTION: Event Bindings
    // ===================================================================================

    $('.tab-btn').on('click', (e: JQuery.ClickEvent) => {
        const $this = $(e.currentTarget);

        if ($this.hasClass('active')) return;

        $('.tab-btn').removeClass('active');
        $this.addClass('active');

        const status = $this.data('status') as string;
        loadFundings(status);
    });

    // ===================================================================================
    //  SECTION: Bootstrap
    // ===================================================================================

    const initialStatus = 'PROCEEDING';
    $(`.tab-btn[data-status="${initialStatus}"]`).addClass('active').siblings().removeClass('active');
    loadFundings(initialStatus);
});