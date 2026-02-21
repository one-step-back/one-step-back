/**
 * @file dashboard.ts
 * @package artist/dashboard
 * @description 아티스트의 수익 현황 데이터를 조회하고 Chart.js를 이용해 시각화하며, 정산 신청 로직을 처리하는 모듈입니다.
 */

import $ from 'jquery';
import {Show} from '@/util/toast';
import {Confirm} from '@/util/confirm';
import {ApiResponse} from '@/types/api';
import Chart from 'chart.js/auto';

/**
 * @interface DashboardSummary
 * @description 대시보드 상단의 요약 수치 데이터 구조입니다.
 */
interface DashboardSummary {
    distributableAmount: number;
    totalSales: number;
    feeAmount: number;
    completedSettlementAmount: number;
}

/**
 * @interface MonthlyRevenue
 * @description 차트 렌더링을 위한 월별 매출 데이터 구조입니다.
 */
interface MonthlyRevenue {
    month: string;
    amount: number;
}

/**
 * @interface DashboardData
 * @description 서버로부터 수신되는 전체 대시보드 데이터 구조입니다.
 */
interface DashboardData {
    summary: DashboardSummary;
    monthlyRevenues: MonthlyRevenue[];
}

$(() => {
    // ===================================================================================
    //  SECTION: DOM Elements & State Management
    // ===================================================================================

    const $btnRequest = $('#btn-request-settlement');
    const $btnRefresh = $('#btn-refresh');
    let myChart: Chart | null = null;

    // ===================================================================================
    //  SECTION: Core Process Functions
    // ===================================================================================

    /**
     * @description 서버에서 대시보드 데이터를 비동기로 불러옵니다.
     * @param onComplete - 로드 완료 후 실행할 콜백 함수 (선택)
     */
    const loadDashboardData = (onComplete?: () => void): void => {
        $.ajax({
            url: '/api/v1/settlements/dashboard',
            type: 'GET',
            success: (res: ApiResponse<DashboardData>) => {
                const dashboard = res.data;
                if (dashboard) {
                    renderSummary(dashboard.summary);
                    renderChart(dashboard.monthlyRevenues);
                    updateRequestButton(dashboard.summary.distributableAmount);
                }
            },
            error: (xhr: JQuery.jqXHR) => {
                console.error("대시보드 로드 실패", xhr);
                Show.error("대시보드 데이터를 불러오지 못했습니다.");
            },
            complete: () => {
                if (onComplete) onComplete();
            }
        });
    };

    /**
     * @description 정산 가능 금액을 서버로 전송하여 출금을 요청합니다.
     */
    const executeSettlementRequest = (): void => {
        const originalText = $btnRequest.text();
        $btnRequest.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i> 신청 중...');

        $.ajax({
            url: '/api/v1/settlements/request',
            type: 'POST',
            success: (res: ApiResponse<string>) => {
                Show.success(res.data || '정산 신청이 완료되었습니다.');
                loadDashboardData();
            },
            error: (xhr: JQuery.jqXHR) => {
                const errorRes = xhr.responseJSON as ApiResponse;
                const msg = errorRes?.error?.message || '정산 신청에 실패했습니다.';
                Show.error(msg);
                $btnRequest.prop('disabled', false).text(originalText);
            }
        });
    };

    // ===================================================================================
    //  SECTION: UI Renderers
    // ===================================================================================

    const formatWon = (val: number): string => {
        return (val || 0).toLocaleString() + '원';
    };

    const renderSummary = (summary: DashboardSummary): void => {
        if (!summary) return;
        $('#val-distributable').text(formatWon(summary.distributableAmount));
        $('#val-total-sales').text(formatWon(summary.totalSales));
        $('#val-fee').text(formatWon(summary.feeAmount));
        $('#val-completed').text(formatWon(summary.completedSettlementAmount));
    };

    const updateRequestButton = (amount: number): void => {
        if (amount > 0) {
            $btnRequest.prop('disabled', false).text('정산 신청하기');
        } else {
            $btnRequest.prop('disabled', true).text('정산 신청하기');
        }
    };

    const renderChart = (monthlyRevenues: MonthlyRevenue[]): void => {
        if (!monthlyRevenues || monthlyRevenues.length === 0) return;

        const canvas = document.getElementById('revenueChart') as HTMLCanvasElement;
        const ctx = canvas?.getContext('2d');
        if (!ctx) return;

        const labels = monthlyRevenues.map(m => m.month);
        const dataPoints = monthlyRevenues.map(m => m.amount);

        if (myChart) myChart.destroy();

        myChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: '매출 (원)',
                    data: dataPoints,
                    borderColor: '#2563eb',
                    backgroundColor: 'rgba(37, 99, 235, 0.1)',
                    borderWidth: 2,
                    tension: 0.3,
                    fill: true,
                    pointBackgroundColor: '#fff',
                    pointBorderColor: '#2563eb',
                    pointRadius: 5
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: {
                    y: {
                        beginAtZero: true,
                        border: { dash: [2, 4] },
                        grid: { color: '#f1f5f9' }
                    },
                    x: {
                        grid: { display: false }
                    }
                }
            }
        });
    };

    // ===================================================================================
    //  SECTION: Event Handlers
    // ===================================================================================

    $btnRefresh.on('click', function (e: JQuery.ClickEvent) {
        const $icon = $(e.currentTarget).find('i');
        $icon.addClass('fa-spin');

        loadDashboardData(() => {
            setTimeout(() => {
                $icon.removeClass('fa-spin');
                Show.success('최신 데이터로 갱신되었습니다.');
            }, 500);
        });
    });

    $btnRequest.on('click', () => {
        Confirm.open({
            title: "정산 신청",
            desc: "현재 정산 가능한 금액을 모두 신청하시겠습니까?\n익일(새벽 4시)에 일괄 지급됩니다.",
            actionText: "신청하기",
            onConfirm: executeSettlementRequest
        });
    });

    // ===================================================================================
    //  SECTION: Bootstrap
    // ===================================================================================

    loadDashboardData();
});