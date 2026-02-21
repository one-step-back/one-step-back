/**
 * @file feed-like.ts
 * @package artist
 * @description 피드 좋아요(Like) 기능을 처리하는 공유 모듈입니다. 리스트 및 상세 페이지에서 공통으로 사용됩니다.
 */

import $ from 'jquery';
import { Show } from '@/util/toast';

/**
 * @function initFeedLikeSystem
 * @description 좋아요 버튼의 클릭 이벤트를 DOM에 바인딩하여 시스템을 초기화합니다.
 */
export function initFeedLikeSystem(): void {
    // 중복 바인딩을 방지하기 위해 off() 후 on()을 적용합니다.
    $(document).off('click', '.btn-like').on('click', '.btn-like', function () {
        const $btn = $(this);
        const $icon = $btn.find('.icon-heart');
        const $countSpan = $btn.find('.like-count');
        const feedId = $btn.data('feed-id') as number;

        const viewerId = $('body').data('member-id') as number | undefined;

        if (!viewerId) {
            Show.error("로그인이 필요한 서비스입니다.");
            return;
        }

        const wasLiked = $btn.hasClass('active');
        const currentCount = parseInt($countSpan.text().replace(/,/g, ''), 10) || 0;
        const targetStatus = !wasLiked;

        if (targetStatus) {
            $btn.addClass('active');
            $icon.removeClass('far').addClass('fas');
            $countSpan.text((currentCount + 1).toLocaleString());
            triggerLikeEffect($btn);
            Show.success("이 피드를 좋아합니다!");
        } else {
            $btn.removeClass('active');
            $icon.removeClass('fas').addClass('far');
            $countSpan.text((currentCount - 1).toLocaleString());
            Show.info("좋아요를 취소했습니다.");
        }

        $.ajax({
            url: `/api/v1/feeds/like?feed-id=${feedId}&status=${targetStatus}`,
            type: 'POST',
            error: () => {
                Show.error("처리에 실패했습니다.");

                if (targetStatus) {
                    $btn.removeClass('active');
                    $icon.removeClass('fas').addClass('far');
                    $countSpan.text(currentCount.toLocaleString());
                } else {
                    $btn.addClass('active');
                    $icon.removeClass('far').addClass('fas');
                    $countSpan.text(currentCount.toLocaleString());
                }
            }
        });
    });

    /**
     * @function triggerLikeEffect
     * @description 좋아요 버튼 클릭 시 일시적인 애니메이션 효과를 부여합니다.
     * @param {JQuery} $btn - 애니메이션을 적용할 제이쿼리 객체
     */
    function triggerLikeEffect($btn: JQuery): void {
        $btn.addClass('artist-btn--animate');
        setTimeout(() => {
            $btn.removeClass('artist-btn--animate');
        }, 800);
    }
}