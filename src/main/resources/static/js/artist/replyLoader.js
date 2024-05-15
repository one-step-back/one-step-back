import {getReplies} from "../service/reply/replyService.js";
import {postRepliesComponent} from "../component/reply/postReplyComponent.js";

let isLoading = false;
let isLastSlice = replyCount === 0;
let page = 1;

window.addEventListener('scroll', function () {
    if (isLoading || isLastSlice) return;

    let {scrollTop, scrollHeight, clientHeight} = document.documentElement;

    if (clientHeight + scrollTop >= scrollHeight) {
        loadReplies();
    }
});

const loadReplies = async () => {
    isLoading = true;

    const {success, message, data} = await getReplies(postId, page);

    if (success) {
        const {replies, isEnd} = data;
        const html = postRepliesComponent(replies);

        $('#replyWrap').append(html)
        isLastSlice = isEnd;
        page++;
    } else {
        alert(message);
    }

    isLoading = false;
}