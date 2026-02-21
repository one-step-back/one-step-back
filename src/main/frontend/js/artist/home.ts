/**
 * @file home.ts
 * @package artist
 * @description 아티스트 홈 화면의 펀딩, 피드, 멤버십 섹션 데이터를 서버로부터 조회하고 렌더링하는 모듈입니다.
 */

import $ from 'jquery';
import {ApiResponse} from '@/types/api';

/**
 * @interface FundingData
 * @description 홈 화면에 노출될 진행 중인 펀딩 데이터 구조입니다.
 */
interface FundingData {
    id: number;
    mainImgPath: string;
    title: string;
    achieveRate: number;
    daysLeft: number;
}

/**
 * @interface FeedData
 * @description 홈 화면에 노출될 최근 피드 데이터 구조입니다.
 */
interface FeedData {
    id: number;
    title: string;
    contentPreview: string;
    minTier: number;
    writeTime: string | number[];
    likeCount: number;
    replyCount: number;
}

/**
 * @interface MembershipData
 * @description 홈 화면에 노출될 멤버십 상품 데이터 구조입니다.
 */
interface MembershipData {
    id: number;
    name: string;
    price: number;
    description: string;
}

/**
 * @interface ArtistHomeResponse
 * @description 아티스트 홈 조회를 위한 종합 데이터 응답 구조입니다.
 */
interface ArtistHomeResponse {
    funding?: FundingData;
    feeds: FeedData[];
    memberships: MembershipData[];
}

$(() => {
    /** @constant {string} artistId - 현재 URL에서 추출한 아티스트의 고유 식별자입니다. */
    const artistId = location.pathname.split('/')[2];

    // ===================================================================================
    //  SECTION: Initialization
    // ===================================================================================

    // 아티스트 홈 대시보드 데이터를 백엔드 API로부터 비동기 호출합니다.
    $.ajax({
        url: `/api/v1/artists/${artistId}/home`,
        type: 'GET',
        success: (response: ApiResponse<ArtistHomeResponse>) => {
            if (response.success && response.data) {
                renderHome(response.data);
            } else {
                console.error("아티스트 홈 데이터를 불러오지 못했습니다.");
            }
        },
        error: (xhr: JQuery.jqXHR, status: string, error: string) => {
            console.error("API Error:", error);
        }
    });

    /**
     * @function renderHome
     * @description 수신된 종합 데이터를 각 섹션별 렌더링 함수로 분배합니다.
     * @param {ArtistHomeResponse} data - 서버로부터 수신한 홈 데이터 객체
     */
    function renderHome(data: ArtistHomeResponse): void {
        renderFundingSection(data.funding);
        renderFeedSection(data.feeds);
        renderMembershipSection(data.memberships);
    }

    // ===================================================================================
    //  SECTION: Render Functions
    // ===================================================================================

    /**
     * @function renderFundingSection
     * @description 진행 중인 펀딩 섹션을 렌더링합니다. 진행 중인 펀딩이 없을 경우 제안 유도 배너를 노출합니다.
     * @param {FundingData | undefined} funding - 펀딩 데이터 객체
     */
    function renderFundingSection(funding?: FundingData): void {
        const $container = $('#funding-highlight-container');
        const $section = $('#funding-highlight-section');
        const $link = $('#link-to-funding');

        if (funding) {
            const html = `
                <a href="/artist/${artistId}/funding/${funding.id}" class="funding-highlight-card">
                    <div class="funding-thumb" style="background-image: url('${funding.mainImgPath}')"></div>
                    <div class="funding-info">
                        <span class="funding-badge">진행 중인 펀딩</span>
                        <h3 class="funding-title">${funding.title}</h3>
                        <div class="funding-stats">
                            <span class="achieve-rate">${funding.achieveRate}% 달성</span>
                            <span class="days-left">D-${funding.daysLeft}</span>
                        </div>
                    </div>
                </a>
            `;
            $container.html(html);
            $link.attr('href', `/artist/${artistId}/funding`);
            $section.show();
        } else {
            /** * @note 진행 중인 프로젝트가 없을 경우 사용자의 펀딩 제안을 유도하는 배너를 렌더링합니다.
             */
            const bannerHtml = `
                <div class="funding-request-banner">
                    <div class="banner-content">
                        <i class="fas fa-bullhorn banner-icon"></i>
                        <div class="banner-text">
                            <h3>진행 중인 프로젝트가 없습니다.</h3>
                            <p>아티스트에게 기가 막힌 펀딩 아이디어를 직접 제안해보세요!</p>
                        </div>
                    </div>
                    <a href="${artistId}/funding/write" class="btn-request-funding">펀딩 제안하기</a>
                </div>
            `;
            $container.html(bannerHtml);
            $link.hide();
            $section.show();
        }
    }

    /**
     * @function renderFeedSection
     * @description 최신 피드 목록을 렌더링합니다. 작성된 피드가 없을 경우 빈 상태(Empty State) 화면을 노출합니다.
     * @param {FeedData[]} feeds - 최신 피드 배열
     */
    function renderFeedSection(feeds: FeedData[]): void {
        const $container = $('#recent-feeds-container');
        const $section = $('#recent-feeds-section');
        const $link = $('#link-to-feed');

        if (feeds && feeds.length > 0) {
            let html = '';
            feeds.forEach(feed => {
                const badgeClass = feed.minTier > 0 ? 'tier-paid' : 'tier-free';
                const badgeText = feed.minTier > 0 ? `Tier ${feed.minTier} 전용` : '전체 공개';

                let dateObj: Date;
                if (Array.isArray(feed.writeTime)) {
                    // Spring LocalDateTime 배열 구조를 방어합니다.
                    dateObj = new Date(feed.writeTime[0], feed.writeTime[1] - 1, feed.writeTime[2]);
                } else {
                    dateObj = new Date(feed.writeTime);
                }

                const dateStr = dateObj.toLocaleDateString('ko-KR', {
                    year: 'numeric', month: '2-digit', day: '2-digit'
                }).replace(/\./g, '.');

                html += `
                    <a href="/artist/${artistId}/feed/${feed.id}" class="feed-card">
                        <span class="feed-tier-badge ${badgeClass}">${badgeText}</span>
                        <h4 class="feed-title">${feed.title}</h4>
                        <p class="feed-preview">${feed.contentPreview}</p>
                        <div class="feed-meta">
                            <span>${dateStr}</span>
                            <span><i class="fas fa-heart"></i> ${feed.likeCount} &nbsp; <i class="fas fa-comment"></i> ${feed.replyCount}</span>
                        </div>
                    </a>
                `;
            });
            $container.html(html);
            $link.attr('href', `/artist/${artistId}/feed`);
            $container.removeClass('empty-feed-grid');
        } else {
            const emptyHtml = `
                <div class="feed-empty-state">
                    <i class="far fa-newspaper"></i>
                    <p>아직 등록된 피드가 없습니다.</p>
                </div>
            `;
            $container.html(emptyHtml);
            $container.addClass('empty-feed-grid');
            $link.hide();
        }
        $section.show();
    }

    /**
     * @function renderMembershipSection
     * @description 멤버십 상품 목록을 렌더링합니다. 등록된 멤버십이 없을 경우 섹션 자체를 렌더링하지 않습니다.
     * @param {MembershipData[]} memberships - 멤버십 상품 배열
     */
    function renderMembershipSection(memberships: MembershipData[]): void {
        const $container = $('#membership-container');
        const $section = $('#membership-section');
        const $link = $('#link-to-membership');

        if (memberships && memberships.length > 0) {
            let html = '';
            memberships.forEach(m => {
                const price = Number(m.price).toLocaleString();
                html += `
                    <div class="membership-card">
                        <h4 class="membership-name">${m.name}</h4>
                        <div class="membership-price">₩${price} <span style="font-size:14px; color:#94a3b8; font-weight:600;">/ 월</span></div>
                        <p class="membership-desc">${m.description}</p>
                    </div>
                `;
            });
            $container.html(html);
            $link.attr('href', `/artist/${artistId}/membership`);
            $section.show();
        } else {
            $section.hide();
        }
    }
});