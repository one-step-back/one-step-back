/**
 * @file my-funding.ts
 * @package my-page
 * @description 마이페이지의 펀딩 참여 내역을 무한 스크롤 방식으로 조회하고 렌더링하는 모듈입니다.
 */

import $ from 'jquery';
import {Show} from '@/util/toast';
import {ApiResponse, SliceResponse} from '@/types/api';

/**
 * @interface FundingHistoryItem
 * @description 서버로부터 수신되는 개별 펀딩 결제 내역의 데이터 구조입니다.
 */
interface FundingHistoryItem {
    paymentId: number;
    artistId: number;
    artistName: string;
    fundingId: number;
    title: string;
    paymentDate: string | number[];
    amount: number;
    status: 'PAID' | 'REFUNDED';
}

$(() => {
    /** @section DOM Elements & State */
    const $fundingList = $('#funding-list');
    const $emptyState = $('#funding-empty');
    const $loading = $('#funding-loading');

    let lastPaymentId: number | null = null;
    let isLoading: boolean = false;
    let hasNext: boolean = true;

    // ===================================================================================
    //  SECTION: Helper Functions
    // ===================================================================================

    /**
     * @function formatNumber
     * @description 금액 데이터를 3자리 단위 콤마가 포함된 문자열로 포맷팅합니다.
     * @param {number | string} amount - 포맷팅할 금액 데이터
     * @returns {string} 포맷팅된 금액 문자열
     */
    const formatNumber = (amount: number | string): string => Number(amount).toLocaleString();

    /**
     * @function formatDate
     * @description Spring의 LocalDateTime 배열 또는 ISO 문자열 형식을 표준 날짜 문자열로 변환합니다.
     * @param {string | number[]} dateData - 서버에서 전달된 날짜 데이터
     * @returns {string} YYYY.MM.DD HH:mm 형식의 날짜 문자열
     */
    const formatDate = (dateData: string | number[]): string => {
        let date: Date;
        if (Array.isArray(dateData)) {
            // [yyyy, MM, dd, HH, mm, ss] 형태로 넘어올 경우를 방어합니다.
            date = new Date(dateData[0], dateData[1] - 1, dateData[2], dateData[3] || 0, dateData[4] || 0);
        } else {
            date = new Date(dateData);
        }

        const yyyy = date.getFullYear();
        const mm = String(date.getMonth() + 1).padStart(2, '0');
        const dd = String(date.getDate()).padStart(2, '0');
        const hh = String(date.getHours()).padStart(2, '0');
        const min = String(date.getMinutes()).padStart(2, '0');
        return `${yyyy}.${mm}.${dd} ${hh}:${min}`;
    };

    // ===================================================================================
    //  SECTION: Core Logic (Infinite Scroll & Fetching)
    // ===================================================================================

    /**
     * @function fetchHistory
     * @description 백엔드 API를 호출하여 펀딩 참여 내역의 페이징 데이터를 가져옵니다.
     */
    const fetchHistory = (): void => {
        if (isLoading || !hasNext) return;

        isLoading = true;
        $loading.show();

        const requestData: any = {size: 5};
        if (lastPaymentId !== null) {
            requestData.lastPaymentId = lastPaymentId;
        }

        $.ajax({
            url: '/api/v1/crowd-funding/my-history',
            type: 'GET',
            data: requestData,
            success: (res: ApiResponse<SliceResponse<FundingHistoryItem>>) => {
                if (res.success && res.data) {
                    const slice = res.data;
                    hasNext = slice.hasNext;
                    const items = slice.content;

                    if (items && items.length > 0) {
                        // 마지막 아이템의 ID를 다음 페이지 요청을 위한 커서(Cursor)로 업데이트합니다.
                        lastPaymentId = items[items.length - 1].paymentId;
                        renderItems(items);
                    } else if (lastPaymentId === null) {
                        // 최초 요청에서 데이터가 존재하지 않는 경우 빈 상태 화면을 노출합니다.
                        $fundingList.hide();
                        $emptyState.show();
                    }
                } else {
                    const msg = res.error?.message || "데이터를 불러오는 중 오류가 발생했습니다.";
                    Show.error(msg);
                }
            },
            error: () => {
                Show.error("서버와의 통신에 실패했습니다.");
            },
            complete: () => {
                isLoading = false;
                $loading.hide();
            }
        });
    };

    /**
     * @constant observer
     * @description 무한 스크롤 구현을 위한 IntersectionObserver 인스턴스입니다.
     */
    const observer = new IntersectionObserver((entries) => {
        const lastEntry = entries[0];
        if (lastEntry.isIntersecting && hasNext && !isLoading) {
            fetchHistory();
        }
    }, {threshold: 0.5});

    /**
     * @function renderItems
     * @description API로부터 전달받은 펀딩 내역 아이템들을 HTML 구조로 렌더링하고 DOM에 추가합니다.
     * @param {FundingHistoryItem[]} items - 렌더링할 아이템 배열
     */
    const renderItems = (items: FundingHistoryItem[]): void => {
        $emptyState.hide();
        $fundingList.show();

        // 기존에 관찰 중이던 마지막 요소의 감시를 일시 해제합니다.
        const $currentLastItem = $fundingList.children().last();
        if ($currentLastItem.length) {
            observer.unobserve($currentLastItem[0]);
        }

        items.forEach(item => {
            const isPaid = item.status === 'PAID';
            const statusClass = isPaid ? 'status-paid' : 'status-refunded';
            const statusText = isPaid ? '결제 완료' : '환불 완료';

            // 상세 페이지로 이동하기 위한 동적 URL 생성
            const detailUrl = `/artist/${item.artistId}/funding/${item.fundingId}`;

            const cardHtml = `
                <li class="funding-card">
                    <div class="card-info">
                        <span class="artist-name">${item.artistName}</span>
                        <a href="${detailUrl}" class="funding-title">${item.title}</a>
                        <span class="payment-date">결제일: ${formatDate(item.paymentDate)}</span>
                    </div>
                    <div class="card-amount-status">
                        <span class="payment-amount">${formatNumber(item.amount)}원</span>
                        <span class="status-badge ${statusClass}">${statusText}</span>
                    </div>
                </li>
            `;
            $fundingList.append(cardHtml);
        });

        // 렌더링된 새로운 리스트의 마지막 요소를 옵저버에 등록하여 스크롤 이벤트를 재활성화합니다.
        const $newLastItem = $fundingList.children().last();
        if ($newLastItem.length && hasNext) {
            observer.observe($newLastItem[0]);
        }
    };

    // ===================================================================================
    //  SECTION: Bootstrap
    // ===================================================================================

    // 스크립트가 로드되면 최초 1회 데이터를 호출하여 화면을 초기화합니다.
    fetchHistory();
});