import {bookmarkService} from "../../service/bookmark/bookmarkService.js";

const isLoggedIn = $('#loggedIn').val() === 'true';

$('.bookmark-btn').click(async function() {
    if (!isLoggedIn) {
        alert("로그인이 필요한 서비스입니다.");
        return;
    }

    const postId = $(this).data('post-id');
    const status = $(this).data('status');

    const result = await bookmarkService.artistPost(postId, status);

    if (result) {
        $(this).find('.far.fa-bookmark').removeClass('active');
        $(this).find('.fas.fa-bookmark').addClass('active');
    } else {
        $(this).find('.far.fa-bookmark').addClass('active');
        $(this).find('.fas.fa-bookmark').removeClass('active');
    }

    $(this).data('status', result);
});
