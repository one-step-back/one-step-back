import {likeService} from "../service/like/likeService.js";

const isLoggedIn = $('#loggedIn').val() === 'true';
const $likeCount = $('#likeCount');

$('.like-btn').click(async function() {
    if (!isLoggedIn) {
        alert("로그인이 필요한 서비스입니다.");
        return;
    }

    const postId = $(this).data('post-id');
    const status = $(this).data('status');

    const result = await likeService(postId, status);

    if (result) {
        $(this).find('.fa-regular.fa-heart').removeClass('active');
        $(this).find('.fa-solid.fa-heart').addClass('active');
        $likeCount.text(parseInt($likeCount.text()) + 1);
    } else {
        $(this).find('.fa-regular.fa-heart').addClass('active');
        $(this).find('.fa-solid.fa-heart').removeClass('active');
        $likeCount.text(parseInt($likeCount.text()) - 1);
    }

    $(this).data('status', result);
});
