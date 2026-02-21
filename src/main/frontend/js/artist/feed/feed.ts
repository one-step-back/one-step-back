/**
 * @file feed.ts
 * @package artist
 * @description 아티스트 피드 목록의 무한 스크롤 로딩, 게시글 삭제 및 상세 페이지 라우팅을 제어하는 모듈입니다.
 */

import $ from 'jquery';
import {Show} from '@/util/toast';
import {Confirm} from '@/util/confirm';
import {ApiResponse, SliceResponse} from '@/types/api';
import {initFeedLikeSystem} from '@/artist/feed/feed-like';
import {timeAgo} from '@/util/timeUtil';

/**
 * @interface FeedItem
 * @description 서버로부터 수신되는 개별 피드 데이터의 구조입니다.
 */
interface FeedItem {
    id: number;
    artistId: number;
    artistNickname: string;
    artistProfilePath?: string;
    title: string;
    content: string;
    writeTime: string | number[];
    category: string;
    likeCount: number;
    replyCount: number;
    liked: boolean;
    locked: boolean;
    requiredMembershipName?: string;
    requiredMembershipId?: number;
    thumbnailPath?: string;
    fileCount: number;
    mediaType: string;
}

$(() => {
    /** @section DOM Elements & State */
    const $feedList = $('#feed-list');
    const $sentinel = $('#loading-sentinel');
    const $spinner = $sentinel.find('.spinner');

    const artistId = $feedList.data('artist-id') as number;
    const viewerId = $('body').data('member-id') as number | undefined;

    let lastFeedId: number | null = null;
    const PAGE_SIZE = 10;
    let hasNext = true;
    let isLoading = false;

    // ===================================================================================
    //  SECTION: Core Logic (Infinite Scroll & Fetching)
    // ===================================================================================

    /**
     * @function loadFeeds
     * @description 서버 API를 호출하여 최신 피드 데이터를 페이징(Slice) 단위로 불러옵니다.
     */
    const loadFeeds = (): void => {
        if (isLoading || !hasNext) return;

        isLoading = true;
        $spinner.addClass('active');

        const requestData: any = {
            artistId: artistId,
            size: PAGE_SIZE
        };
        if (lastFeedId !== null) {
            requestData.lastFeedId = lastFeedId;
        }

        $.ajax({
            url: '/api/v1/feeds/list',
            type: 'GET',
            data: requestData,
            success: (res: ApiResponse<SliceResponse<FeedItem>>) => {
                const data = res.data;
                if (!data) return;

                if (data.content && data.content.length > 0) {
                    renderFeeds(data.content);
                    lastFeedId = data.content[data.content.length - 1].id;
                } else if (lastFeedId === null) {
                    $feedList.html(`
                        <div class="feed-empty-state">
                            <i class="far fa-newspaper icon"></i>
                            <p class="message">아직 등록된 피드가 없습니다.</p>
                            <p class="sub-message">아티스트의 새로운 소식을 기다려주세요!</p>
                        </div>
                    `);
                    if (observer) observer.unobserve($sentinel[0]);
                    $sentinel.hide();
                    return;
                }

                hasNext = data.hasNext;
                if (!hasNext) {
                    if (observer) observer.unobserve($sentinel[0]);
                    $sentinel.hide();
                }
            },
            error: (xhr: JQuery.jqXHR) => {
                console.error("Error loading feeds:", xhr);
                Show.error("피드를 불러오는데 실패했습니다.");
            },
            complete: () => {
                isLoading = false;
                $spinner.removeClass('active');
            }
        });
    };

    /**
     * @constant observer
     * @description 무한 스크롤을 구현하기 위해 최하단 DOM 요소의 노출 여부를 감지하는 옵저버입니다.
     */
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting && hasNext) loadFeeds();
        });
    }, {threshold: 0.1});

    /**
     * @function renderFeeds
     * @description 수신한 피드 데이터 배열을 HTML 요소로 변환하여 화면에 렌더링합니다.
     * @param {FeedItem[]} feeds - 렌더링할 피드 데이터 목록
     */
    const renderFeeds = (feeds: FeedItem[]): void => {
        let htmlBuffer = "";

        feeds.forEach((feed) => {
            const isLocked = feed.locked || false;
            const timeAgoStr = timeAgo(feed.writeTime);
            const profilePath = feed.artistProfilePath || '/images/default-profile.png';

            if (isLocked) {
                const membershipName = feed.requiredMembershipName || '멤버십';
                const membershipId = feed.requiredMembershipId;

                const targetUrl = membershipId
                    ? `/artist/${feed.artistId}/membership/${membershipId}/confirm`
                    : `/artist/${feed.artistId}/membership`;

                htmlBuffer += `
                    <article class="feed-card feed-card--locked" data-feed-id="${feed.id}">
                        <div class="card-header">
                            <img src="${profilePath}" alt="프로필" class="profile-img">
                            <div class="profile-info">
                                <span class="nickname">${feed.artistNickname}</span>
                                <span class="timestamp">${timeAgoStr}</span>
                            </div>
                            <div class="header-actions">
                                <span class="category-badge"><i class="fas fa-lock"></i> ${membershipName}</span>
                            </div>
                        </div>

                        <div class="card-media" style="height: 200px; background: #eee;"></div>
                        <div class="card-body">
                            <h3 class="card-title">${membershipName} 전용 게시글입니다.</h3>
                            <div class="card-content-text">이 콘텐츠를 확인하려면 <strong>${membershipName}</strong> 구독이 필요합니다.</div>
                        </div>

                        <div class="lock-overlay">
                            <div class="lock-icon-box">
                                <i class="fas fa-lock"></i>
                            </div>
                            <p class="lock-message">이 게시글은 잠겨있습니다</p>
                            <p class="lock-sub-message">${membershipName}에 가입하고 아티스트를 후원하세요.</p>
                            <button type="button" class="btn-subscribe-req" data-url="${targetUrl}">
                                ${membershipName} 구독하고 전체 보기
                            </button>
                        </div>
                    </article>
                `;
                return;
            }

            let mediaHtml = "";
            if (feed.thumbnailPath) {
                const multiIcon = feed.fileCount > 1
                    ? `<div class="multi-files-icon"><i class="fas fa-layer-group"></i> +${feed.fileCount - 1}</div>`
                    : "";

                if (feed.mediaType === "VIDEO") {
                    mediaHtml = `<div class="card-media"><video src="${feed.thumbnailPath}" muted loop playsinline></video>${multiIcon}</div>`;
                } else {
                    mediaHtml = `<div class="card-media"><img src="${feed.thumbnailPath}" alt="이미지" loading="lazy">${multiIcon}</div>`;
                }
            }

            const likeBtnClass = feed.liked ? 'active' : '';
            const heartIconClass = feed.liked ? 'fas' : 'far';

            let moreMenuHtml = "";
            if (viewerId && viewerId === feed.artistId) {
                moreMenuHtml = `
                    <div class="more-menu-wrapper">
                        <button type="button" class="action-btn btn-more btn-feed-more"><i class="fas fa-ellipsis-h"></i></button>
                        <div class="dropdown-menu">
                            <a href="/artist/${artistId}/feed/write?id=${feed.id}" class="dropdown-item"><i class="fas fa-pen"></i> 수정</a>
                            <button class="dropdown-item btn-delete" data-feed-id="${feed.id}"><i class="fas fa-trash"></i> 삭제</button>
                        </div>
                    </div>`;
            }

            htmlBuffer += `
                <article class="feed-card" data-feed-id="${feed.id}">
                    <div class="card-header">
                        <img src="${profilePath}" alt="프로필" class="profile-img">
                        <div class="profile-info">
                            <span class="nickname">${feed.artistNickname}</span>
                            <span class="timestamp">${timeAgoStr}</span>
                        </div>
                        <div class="header-actions">
                            <span class="category-badge">${feed.category}</span>
                            ${moreMenuHtml}
                        </div>
                    </div>
                    ${mediaHtml}
                    <div class="card-body">
                        <h3 class="card-title">${feed.title}</h3>
                        <div class="card-content-text">${feed.content}</div>
                    </div>
                    <div class="card-footer">
                        <button class="action-btn btn-like ${likeBtnClass}" data-feed-id="${feed.id}">
                            <i class="${heartIconClass} fa-heart icon-heart"></i>
                            <span class="like-count">${feed.likeCount}</span>
                        </button>
                        <button class="action-btn">
                            <i class="far fa-comment"></i>
                            <span>${feed.replyCount}</span>
                        </button>
                    </div>
                </article>
            `;
        });

        $feedList.append(htmlBuffer);
    };

    // ===================================================================================
    //  SECTION: Event Handlers
    // ===================================================================================

    /**
     * @description 피드 카드 클릭 시 상세 페이지로 라우팅합니다. 버튼 및 팝업 요소 클릭 시 라우팅을 차단합니다.
     */
    $feedList.on('click', '.feed-card', function (e: JQuery.ClickEvent) {
        const $card = $(this);

        if ($card.hasClass('feed-card--locked')) {
            Show.info("멤버십 구독이 필요한 게시글입니다.");
            return;
        }

        if ($(e.target as HTMLElement).closest(".action-btn, .profile-info, .more-menu-wrapper, .dropdown-item, .btn-subscribe-req").length) {
            return;
        }

        const feedId = $card.data('feed-id');
        location.href = `/artist/${artistId}/feed/view?id=${feedId}`;
    });

    /**
     * @description 잠긴 게시글의 구독 버튼 클릭 이벤트를 처리합니다.
     */
    $feedList.on('click', '.btn-subscribe-req', function (e: JQuery.ClickEvent) {
        e.stopPropagation();
        e.preventDefault();
        const targetUrl = $(this).data('url') as string;
        if (targetUrl) {
            location.href = targetUrl;
        }
    });

    /**
     * @description 더보기 메뉴를 토글합니다.
     */
    $feedList.on('click', '.btn-feed-more', function (e: JQuery.ClickEvent) {
        e.stopPropagation();
        $(".more-menu-wrapper").not($(this).parent()).removeClass("active");
        $(this).parent().toggleClass("active");
    });

    /**
     * @description 영역 외 클릭 시 활성화된 더보기 메뉴를 비활성화합니다.
     */
    $(document).on('click', () => {
        $(".more-menu-wrapper").removeClass("active");
    });

    /**
     * @description 게시글 삭제 요청을 처리하며, 성공 시 UI에서 즉시 요소를 제거합니다.
     */
    $feedList.on('click', '.btn-delete', function (e: JQuery.ClickEvent) {
        e.stopPropagation();
        const feedId = $(this).data('feed-id') as number;

        Confirm.open({
            title: "게시글 삭제",
            desc: "정말 이 게시글을 삭제하시겠습니까?",
            actionText: "삭제",
            onConfirm: () => {
                $.ajax({
                    url: `/api/v1/feeds/${feedId}`,
                    type: "DELETE",
                    success: () => {
                        Show.success("게시글이 삭제되었습니다.");
                        $(`article[data-feed-id='${feedId}']`).fadeOut(300, function () {
                            $(this).remove();
                            if ($feedList.children('article').length === 0) {
                                location.reload();
                            }
                        });
                    },
                    error: () => {
                        Show.error("삭제 실패");
                    }
                });
            }
        });
    });

    // ===================================================================================
    //  SECTION: Bootstrap
    // ===================================================================================

    // 공통 좋아요 시스템을 초기화합니다.
    initFeedLikeSystem();

    // 무한 스크롤 감지 및 초기 데이터 로드
    if ($sentinel.length > 0) observer.observe($sentinel[0]);
    loadFeeds();
});