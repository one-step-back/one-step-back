import {getTimeGapFromNow} from "../../util/timeUtil.js";

export const postRepliesComponent = (data) => {
    let html = '';

    data.forEach(reply => {
        const {
            replyId,
            memberId,
            content,
            createdTime,
            nickname,
            kakaoProfileUrl,
            profileName,
            profilePath
        } = reply;

        html += `
            <div class="reply-item">
                <a class="replier-avatar">
                ${kakaoProfileUrl == null && profileName == null ? `
                    <img src="/images/none-avatar.svg">
                ` : ''}
                ${kakaoProfileUrl != null && profileName == null ? `
                    <img src="${kakaoProfileUrl}">
                ` : ''}
                </a>
                <div class="reply-body">
                    <div class="reply-head">
                        <a class="replier-name">${nickname}</a>
                         <time class="reply-time">${getTimeGapFromNow(createdTime)}</time>
                    </div>
                    <pre class="reply-content">${content}</pre>
                </div>
            </div>
        `;
    });

    return html;
}

export const postReplyComponent = (data) => {
    const {
        replyId,
        memberId,
        content,
        createdTime,
        nickname,
        kakaoProfileUrl,
        profileName,
        profilePath
    } = data;

    return `
        <div class="reply-item">
            <a class="replier-avatar">
            ${kakaoProfileUrl == null && profileName == null ? `
                <img src="/images/none-avatar.svg">
            ` : ''}
            ${kakaoProfileUrl != null && profileName == null ? `
                <img src="${kakaoProfileUrl}">
            ` : ''}
            </a>
            <div class="reply-body">
                <div class="reply-head">
                    <a class="replier-name">${nickname}</a>
                     <time class="reply-time">${getTimeGapFromNow(createdTime)}</time>
                </div>
                <pre class="reply-content">${content}</pre>
            </div>
        </div>
    `;
}