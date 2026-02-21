/**
 * @file feed-view.ts
 * @package artist
 * @description 아티스트 피드 상세 페이지의 미디어 슬라이더, 게시글 관리(삭제) 및 좋아요/댓글 모듈을 초기화하는 엔트리 포인트입니다.
 */

import $ from 'jquery';
import { Show } from '@/util/toast';
import { Confirm } from '@/util/confirm';
import { initFeedLikeSystem } from './feed-like';
import { initFeedReplySystem } from './feed-reply';

$(() => {
    // ===================================================================================
    //  SECTION: Locked Post Action
    // ===================================================================================

    $('.btn-subscribe-req').on('click', function (e: JQuery.ClickEvent) {
        e.stopPropagation();
        e.preventDefault();
        const url = $(this).data('url') as string;
        if (url) {
            location.href = url;
        }
    });

    // ===================================================================================
    //  SECTION: Media Slider Logic
    // ===================================================================================

    const $track = $('.slider-track');
    let $slides = $('.slide-item');
    const originalCount = $slides.length;

    if (originalCount > 1) {
        const $firstClone = $slides.first().clone();
        const $lastClone = $slides.last().clone();

        $track.append($firstClone);
        $track.prepend($lastClone);

        $slides = $('.slide-item');
        const totalCount = $slides.length;

        let currentIndex = 1;
        const $currentIdxEl = $('.current-idx');
        const $prevBtn = $('.prev-btn');
        const $nextBtn = $('.next-btn');

        let isAnimating = false;
        const transitionTime = 300;

        $track.css({
            'transform': `translateX(-${currentIndex * 100}%)`,
            'transition': 'none'
        });

        const moveSlide = (index: number): void => {
            if (isAnimating) return;
            isAnimating = true;

            currentIndex = index;

            $track.css('transition', `transform ${transitionTime}ms ease-in-out`);
            $track.css('transform', `translateX(-${currentIndex * 100}%)`);

            $('video').each(function () {
                (this as HTMLVideoElement).pause();
            });

            let realIndex = currentIndex;
            if (currentIndex === 0) realIndex = originalCount;
            if (currentIndex === totalCount - 1) realIndex = 1;
            $currentIdxEl.text(realIndex);

            setTimeout(() => {
                if (currentIndex === totalCount - 1) {
                    currentIndex = 1;
                    $track.css('transition', 'none');
                    $track.css('transform', `translateX(-${currentIndex * 100}%)`);
                }

                if (currentIndex === 0) {
                    currentIndex = originalCount;
                    $track.css('transition', 'none');
                    $track.css('transform', `translateX(-${currentIndex * 100}%)`);
                }

                isAnimating = false;
            }, transitionTime);
        };

        $prevBtn.on('click', () => moveSlide(currentIndex - 1));
        $nextBtn.on('click', () => moveSlide(currentIndex + 1));
    }

    // ===================================================================================
    //  SECTION: Post Management
    // ===================================================================================

    $('#btn-feed-more').on('click', function (e: JQuery.ClickEvent) {
        e.stopPropagation();
        const $wrapper = $(this).closest('.more-menu-wrapper');
        $wrapper.toggleClass('active');
    });

    $(document).on('click', () => {
        $('.more-menu-wrapper').removeClass('active');
    });

    $('.dropdown-menu').on('click', (e: JQuery.ClickEvent) => {
        e.stopPropagation();
    });

    $('.btn-delete').on('click', function () {
        const feedId = $(this).data('feed-id') as number;
        const artistId = $('#feed-main-container').data('artist-id') as number;

        Confirm.open({
            title: "게시글 삭제",
            desc: "정말 이 게시글을 삭제하시겠습니까?",
            actionText: "삭제",
            onConfirm: () => {
                $.ajax({
                    url: `/api/v1/feeds/${feedId}`,
                    type: 'DELETE',
                    success: () => {
                        Show.success('게시글이 삭제되었습니다.');
                        setTimeout(() => location.href = `/artist/${artistId}/feed`, 1000);
                    },
                    error: (xhr: JQuery.jqXHR) => {
                        Show.error('삭제 실패: ' + (xhr.responseText || '권한이 없습니다.'));
                    }
                });
            }
        });
    });

    // ===================================================================================
    //  SECTION: Module Initialization
    // ===================================================================================

    // 좋아요 모듈 및 댓글 모듈을 주입하여 실행합니다.
    initFeedLikeSystem();
    initFeedReplySystem();
});