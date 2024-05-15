import {registerReply} from "../service/reply/replyService.js";
import {postReplyComponent} from "../component/reply/postReplyComponent.js";

const isLoggedIn = $('#loggedIn').val() === 'true';
const $replyTextarea = $('#replyTextarea');

$('#submitReply').click(async function () {
    if (!isLoggedIn) {
        alert("로그인이 필요한 서비스입니다.");
        return;
    }

    if ($replyTextarea.val() === '') {
        alert("내용을 입력해주세요.");
        return;
    }

    const content = $replyTextarea.val();

    const {success, message, data} = await registerReply(postId, content);

    if (success) {
        const html = postReplyComponent(data);

        $('#replyWrap').prepend(html);
        $replyTextarea.val('');
    } else {
        alert(message);
    }
});