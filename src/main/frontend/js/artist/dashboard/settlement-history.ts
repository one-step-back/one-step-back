/**
 * @file settlement-history.ts
 * @package artist/dashboard
 * @description 아티스트의 과거 정산 신청 및 완료 내역을 무한 스크롤(Slice) 방식으로 조회하여 렌더링하는 모듈입니다.
 */

import $ from 'jquery';
import {Show} from '@/util/toast';
import {ApiResponse, SliceResponse} from '@/types/api';

/**
 * @interface SettlementHistoryItem
 * @description 서버로부터 수신되는 개별 정산 내역의 데이터 구조입니다.
 */
interface SettlementHistoryItem {
    id: number;
    status: string;
    requestTime?: string;
    completeTime?: string;
    totalAmount: number;
    feeAmount: number;
    netAmount: number;
}

$(() => {
    // ===================================================================================
    //  SECTION: State Management
    // ===================================================================================

    let lastHistoryId: number | null = null;
    let historyHasNext: boolean = true;
    let isHistoryLoading: boolean = false;

    // ===================================================================================
    //  SECTION: Formatting & Helper Functions
    // ===================================================================================

    /**
     * @description 금액 데이터를 한국 원화 단위 포맷으로 변환합니다.
     */
    const formatWon = (val: number): string => {
        return (val || 0).toLocaleString() + '원';
    };

    /**
     * @description 영문 상태 코드를 사용자 친화적인 한글 라벨로 변환합니다.
     */
    const getStatusLabel = (status: string): string => {
        switch (status) {
            case 'REQUESTED':
                return '지급 대기';
            case 'COMPLETED':
                return '지급 완료';
            case 'REJECTED':
                return '반려됨';
            default:
                return status;
        }
    };

    /**
     * @description 더보기 버튼의 노출 여부를 다음 페이지 존재 유무(hasNext)에 따라 업데이트합니다.
     */
    const updateMoreButton = (): void => {
        const $btnMore = $('#btn-load-more');
        if (historyHasNext) {
            $btnMore.html('더보기 <i class="fas fa-chevron-down"></i>').show();
        } else {
            $btnMore.hide();
        }
    };

    /**
     * @description 수신된 정산 내역 데이터를 기반으로 테이블 행(Row)을 렌더링합니다.
     */
    const renderHistoryTable = (list: SettlementHistoryItem[]): void => {
        const $tbody = $('#history-list-body');
        let html = '';

        list.forEach(item => {
            const statusLabel = getStatusLabel(item.status);
            const statusClass = 'status-' + item.status;

            const reqDate = item.requestTime ? item.requestTime.split('T')[0] : '-';
            const compDate = item.completeTime ? item.completeTime.split('T')[0] : '-';

            html += `
                <tr>
                    <td>${reqDate}</td>
                    <td>${formatWon(item.totalAmount)}</td>
                    <td class="text-danger">-${formatWon(item.feeAmount)}</td>
                    <td style="font-weight:700;">${formatWon(item.netAmount)}</td>
                    <td><span class="status-badge ${statusClass}">${statusLabel}</span></td>
                    <td>${compDate}</td>
                </tr>
            `;
        });

        $tbody.append(html);
    };

    // ===================================================================================
    //  SECTION: Core Process Functions
    // ===================================================================================

    /**
     * @description 서버 API를 호출하여 정산 내역 데이터를 페이징 단위로 로드합니다.
     */
    const loadHistoryData = (): void => {
        if (!historyHasNext || isHistoryLoading) return;

        isHistoryLoading = true;
        const $btnMore = $('#btn-load-more');
        $btnMore.html('<i class="fas fa-spinner fa-spin"></i> 불러오는 중...');

        let url = '/api/v1/settlements/history';
        if (lastHistoryId !== null) {
            url += `?lastId=${lastHistoryId}`;
        }

        $.ajax({
            url: url,
            type: 'GET',
            success: (response: ApiResponse<SliceResponse<SettlementHistoryItem>>) => {
                // ApiResponse 도입 과도기를 대비한 방어 코드
                const slice = response.data || (response as unknown as SliceResponse<SettlementHistoryItem>);
                const list = slice.content || [];
                historyHasNext = slice.hasNext;

                if (list.length > 0) {
                    renderHistoryTable(list);
                    lastHistoryId = list[list.length - 1].id;
                    $('#history-empty').hide();
                } else {
                    if (lastHistoryId === null) $('#history-empty').show();
                }

                updateMoreButton();
            },
            error: (xhr: JQuery.jqXHR) => {
                console.error("내역 로드 실패", xhr);
                Show.error("정산 내역을 불러오지 못했습니다.");
            },
            complete: () => {
                isHistoryLoading = false;
            }
        });
    };

    // ===================================================================================
    //  SECTION: Event Bindings & Bootstrap
    // ===================================================================================

    $('#btn-load-more').on('click', () => {
        loadHistoryData();
    });

    // 스크립트 로드 시 최초 1회 데이터 호출
    loadHistoryData();
});