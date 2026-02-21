/**
 * @file index.ts
 * @description 메인 페이지 대시보드 데이터 바인딩 및 동적 리스트 렌더링 (TypeScript)
 */

import $ from 'jquery';
import { timeAgo } from '@/util/timeUtil';
import { ApiResponse } from '@/types/api';

/** 인기 아티스트 응답 객체 인터페이스 */
interface RisingArtist {
    id: number;
    name: string;
    description?: string;
    profilePath?: string;
    followerCount: number;
}

/** 주목할 펀딩 응답 객체 인터페이스 */
interface SpotlightFunding {
    id: number;
    artistId: number;
    title: string;
    artistName: string;
    thumbnailPath?: string;
    achieveRate: number;
    daysLeft: number;
}

/** 실시간 피드 응답 객체 인터페이스 */
interface LiveFeed {
    id: number;
    artistId: number;
    artistName: string;
    artistProfilePath?: string;
    contentPreview: string;
    writeTime: string;
}

/** 메인 페이지 통합 데이터 인터페이스 */
interface MainData {
    risingArtists: RisingArtist[];
    fundings: SpotlightFunding[];
    feeds: LiveFeed[];
}

$(() => {
    /** * 메인 페이지 통합 데이터 API 호출
     * 인기 아티스트, 펀딩, 피드를 한 번에 로드한다.
     */
    $.ajax({
        url: '/api/v1/main',
        type: 'GET',
        success: (res: ApiResponse<MainData>) => {
            // ApiResponse 규격에 맞게 data 추출
            const data = res.data;
            if (data) {
                renderRisingArtists(data.risingArtists);
                renderSpotlightFunding(data.fundings);
                renderLiveFeeds(data.feeds);
            }
        },
        error: (xhr: JQuery.jqXHR) => {
            console.error("메인 데이터 로드 실패", xhr);
        }
    });

    /** * 급상승 아티스트 섹션 렌더링
     */
    function renderRisingArtists(artists: RisingArtist[]): void {
        const container = $('#rising-artist-list');
        if (!artists || !artists.length) {
            container.html(`
                <div class="no-data-container">
                    <i class="fas fa-user-slash no-data-icon"></i>
                    <p class="no-data-text">아직 등록된 아티스트가 없습니다.</p>
                </div>
            `);
            return;
        }

        let html = '';
        artists.forEach(a => {
            const profile = a.profilePath || '/images/default-profile.png';
            const followerStr = a.followerCount > 999 ? (a.followerCount / 1000).toFixed(1) + 'K' : a.followerCount;
            html += `
                <a href="/artist/${a.id}" class="artist-card-v2">
                    <img src="${profile}" class="artist-avatar-large" alt="${a.name}">
                    <h3 class="artist-name">${a.name}</h3>
                    <p class="artist-tags">${a.description || '소개글이 없습니다.'}</p>
                    <div class="btn-follow-sm">팔로우 ${followerStr}</div>
                </a>
            `;
        });
        container.html(html);
    }

    /** * 주목할 펀딩 섹션 렌더링
     */
    function renderSpotlightFunding(fundings: SpotlightFunding[]): void {
        const container = $('#spotlight-funding-list');
        if (!fundings || !fundings.length) {
            container.html(`
                <div class="no-data-container">
                    <i class="fas fa-search-dollar no-data-icon"></i>
                    <p class="no-data-text">진행 중인 펀딩 프로젝트가 없습니다.</p>
                </div>
            `);
            return;
        }

        let html = '';
        fundings.forEach(f => {
            const progressWidth = Math.min(f.achieveRate, 100);
            const thumb = f.thumbnailPath || '/images/no-image.png';
            html += `
                <a href="/artist/${f.artistId}/funding/${f.id}" class="funding-card-v2">
                    <div class="funding-thumb-v2" style="background-image: url('${thumb}')">
                        <span class="funding-badge-dday">D-${f.daysLeft}</span>
                    </div>
                    <div class="funding-body">
                        <span class="funding-artist-name">${f.artistName}</span>
                        <h3 class="funding-title-v2">${f.title}</h3>
                        <div class="funding-progress-area">
                            <div class="progress-info"><span>${f.achieveRate}% 달성</span></div>
                            <div class="progress-bar-bg"><div class="progress-bar-fill" style="width: ${progressWidth}%"></div></div>
                        </div>
                    </div>
                </a>
            `;
        });
        container.html(html);
    }

    /** * 실시간 피드 섹션 렌더링
     */
    function renderLiveFeeds(feeds: LiveFeed[]): void {
        const container = $('#live-feed-list');
        if (!feeds || !feeds.length) {
            container.html(`
                <div class="no-data-container">
                    <i class="far fa-comment-dots no-data-icon"></i>
                    <p class="no-data-text">최근 게시된 피드가 없습니다.</p>
                </div>
            `);
            return;
        }

        let html = '';
        feeds.forEach(f => {
            const timeStr = timeAgo(f.writeTime);
            const profile = f.artistProfilePath || '/images/default-profile.png';
            html += `
                <a href="/artist/${f.artistId}/feed/view?id=${f.id}" class="feed-mini-card">
                    <div class="feed-mini-header">
                        <img src="${profile}" class="feed-avatar-xs" alt="프로필">
                        <span class="feed-mini-artist">${f.artistName}</span>
                    </div>
                    <p class="feed-mini-text">${f.contentPreview}</p>
                    <span class="feed-mini-meta">${timeStr}</span>
                </a>
            `;
        });
        container.html(html);
    }
});