import {subscriptionService} from "../service/subscription/subscriptionService.js";

const $subscriberCount = $('#subscriberCount');

// 아티스트 구독
$('#subscribeBtn').click(async function () {
    try {
        const artistId = $(this).data('artistid');
        const status = $(this).data('status');

        const result = await subscriptionService(artistId, status);

        $(this).empty();

        if (result) {
            $(this).append(`<span class="active-text">구독중</span>`);
            $(this).addClass('active');
            $subscriberCount.text(parseInt($subscriberCount.text()) + 1);
        } else {
            $(this).append(`<span class="default-text">구독하기</span>`);
            $(this).removeClass('active');
            $subscriberCount.text(parseInt($subscriberCount.text()) - 1);
        }

        $(this).data('status', result);
    } catch (e) {
        console.error("구독 오류 발생, {}", e.getError());
        alert('구독중 서버 오류가 발생했습니다. 나중에 다시 시도해주세요.');
    }
});
