/**
 * @file feed-reply.ts
 * @package artist
 * @description 피드 상세 페이지의 댓글(Comment) 및 대댓글(Reply) 시스템을 제어하는 모듈입니다.
 */

import $ from 'jquery';
import { Show } from '@/util/toast';
import { Confirm } from '@/util/confirm';
import { timeAgo } from '@/util/timeUtil';
import 'jquery-color';

/**
 * @interface CommentData
 * @description 서버로부터 수신되는 댓글/대댓글의 데이터 구조입니다.
 */
interface CommentData {
    id: number | string;
    memberId: number;
    memberNickname: string;
    memberProfile: string;
    content: string;
    createdTime: string | number[];
    updatedTime: string | number[];
    replyCount?: number;
    targetMemberNickname?: string | null;
}

/**
 * @function initFeedReplySystem
 * @description 댓글 시스템에 필요한 모든 DOM 이벤트를 초기화하고 첫 번째 페이지를 로드합니다.
 */
export function initFeedReplySystem(): void {
    // ===================================================================================
    //  SECTION: State & DOM Elements
    // ===================================================================================
    const $feedContainer = $('#feed-main-container');
    const $replyList = $('#reply-list');
    const $btnMore = $('#btn-reply-more');
    const $inputContent = $('#reply-content');
    const $btnWrite = $('#btn-reply-write');
    const $countBadge = $('#reply-count-badge');

    const feedId = $feedContainer.data('feed-id') as number;
    const feedWriterId = $feedContainer.data('artist-id') as number;
    const currentMemberId = $('body').data('member-id') as number | undefined;

    const $userInfo = $('#current-user-info');
    const myNickname = $userInfo.data('member-nickname') as string;
    const myProfilePath = $userInfo.data('member-profile') as string;

    let lastCommentId: number | null = null;
    const PAGE_SIZE = 10;
    let hasNext = true;
    let isLoading = false;

    // 만약 로그인 정보가 없다면 댓글 기능 자체를 초기화하지 않고 종료합니다.
    if (!currentMemberId) return;

    // ===================================================================================
    //  SECTION: Helper Functions
    // ===================================================================================

    const updateBadgeCount = (diff: number): void => {
        const current = parseInt($countBadge.text(), 10) || 0;
        const next = Math.max(0, current + diff);
        $countBadge.text(next);
    };

    const createCommentItem = (data: CommentData): string => {
        const isMine = (data.memberId === currentMemberId);
        const timeString = timeAgo(data.createdTime);
        const edited = (data.updatedTime && JSON.stringify(data.createdTime) !== JSON.stringify(data.updatedTime))
            ? '<span class="edited-badge">(수정됨)</span>' : '';

        const btns = isMine
            ? `<button type="button" class="btn-action btn-comment-edit">수정</button>
               <button type="button" class="btn-action btn-delete btn-comment-delete">삭제</button>`
            : '';

        return `
            <div class="comment-item" data-comment-id="${data.id}" data-member-id="${data.memberId}">
                <a href="/artist/${data.memberId}" class="comment-profile"><img src="${data.memberProfile}" alt="프사"></a>
                <div class="comment-body">
                    <div class="comment-content-wrapper">
                        <a href="/artist/${data.memberId}" class="writer">${data.memberNickname}</a>
                        <span class="comment-text">${data.content.replace(/\n/g, '<br>')}</span>
                    </div>
                    <div class="comment-meta-actions">
                        <span class="date">${timeString}${edited}</span>
                        <button type="button" class="btn-action btn-reply-toggle">답글달기</button>
                        ${btns}
                    </div>
                    ${data.replyCount && data.replyCount > 0 ? `<div class="replies-wrapper"><button class="btn-view-replies">답글 ${data.replyCount}개 보기</button></div>` : ''}
                </div>
            </div>`;
    };

    const createReplyItem = (data: CommentData): string => {
        const isMine = (data.memberId === currentMemberId);
        const timeString = timeAgo(data.createdTime);
        const edited = (data.updatedTime && JSON.stringify(data.createdTime) !== JSON.stringify(data.updatedTime))
            ? '<span class="edited-badge">(수정됨)</span>' : '';

        const btns = isMine
            ? `<button type="button" class="btn-action btn-reply-edit">수정</button>
               <button type="button" class="btn-action btn-delete btn-reply-delete">삭제</button>`
            : '';

        let content = data.content.replace(/\n/g, '<br>');
        if (data.targetMemberNickname) {
            content = `<span class="mention-target">@${data.targetMemberNickname}</span> ` + content;
        }

        return `
            <div class="reply-item" data-reply-id="${data.id}" data-member-id="${data.memberId}" style="display: flex; gap: 10px; margin-top: 12px;">
                <a href="/artist/${data.memberId}" class="reply-profile"><img src="${data.memberProfile}" alt="프사" style="width: 24px; height: 24px; border-radius: 50%; object-fit: cover;"></a>
                <div class="reply-body" style="flex: 1;">
                    <div class="reply-content-wrapper">
                        <a href="/artist/${data.memberId}" class="writer" style="font-weight: 600; font-size: 0.9rem; color: #333; text-decoration: none; margin-right: 6px;">${data.memberNickname}</a>
                        <span class="reply-text" style="font-size: 0.9rem; color: #333;">${content}</span>
                    </div>
                    <div class="reply-meta-actions" style="margin-top: 4px; font-size: 0.75rem; color: #999;">
                        <span class="date">${timeString}${edited}</span>
                        <button type="button" class="btn-action btn-nested-reply">답글</button>
                        ${btns}
                    </div>
                </div>
            </div>`;
    };

    const loadReplies = (): void => {
        if (isLoading || !hasNext) return;
        isLoading = true;

        const requestData: any = { feedId: feedId, size: PAGE_SIZE };
        if (lastCommentId !== null) requestData.lastCommentId = lastCommentId;

        $.ajax({
            url: '/api/v1/comments/list',
            type: 'GET',
            data: requestData,
            success: (res: any) => {
                const list = res.content || [];
                hasNext = res.hasNext;

                if ((!list || list.length === 0) && lastCommentId === null) {
                    $replyList.html('<div class="empty-message">첫 번째 댓글을 남겨보세요!</div>');
                    $btnMore.hide();
                } else if (list.length > 0) {
                    let html = '';
                    list.forEach((r: CommentData) => html += createCommentItem(r));

                    if (lastCommentId === null) {
                        $replyList.html(html);
                    } else {
                        $replyList.append(html);
                    }
                    lastCommentId = list[list.length - 1].id;
                }

                $btnMore.toggle(hasNext);
                isLoading = false;
            },
            error: (xhr: JQuery.jqXHR) => {
                console.error("댓글 로드 실패", xhr);
                isLoading = false;
            }
        });
    };

    // ===================================================================================
    //  SECTION: Event Bindings (Comments)
    // ===================================================================================

    $inputContent.off('input').on('input', function (this: HTMLTextAreaElement) {
        const val = $(this).val() as string;
        $btnWrite.prop('disabled', val.trim().length === 0);
        this.style.height = 'auto';
        this.style.height = (this.scrollHeight) + 'px';
    });

    $btnWrite.off('click').on('click', () => {
        const content = ($inputContent.val() as string).trim();
        if (!content) return;

        const tempId = 'temp_' + Date.now();
        const nowIso = new Date().toISOString();

        const fakeComment: CommentData = {
            id: tempId,
            memberId: currentMemberId,
            memberNickname: myNickname,
            memberProfile: myProfilePath,
            content: content,
            createdTime: nowIso,
            updatedTime: nowIso,
            replyCount: 0
        };

        const html = createCommentItem(fakeComment);
        $replyList.prepend(html);
        const $newItem = $replyList.find(`[data-comment-id="${tempId}"]`);

        $newItem.css('background-color', '#f0f8ff').animate({backgroundColor: '#fff'}, 1000);

        $inputContent.val('').css('height', 'auto').trigger('input');
        updateBadgeCount(1);
        $('.empty-message').remove();

        $.ajax({
            url: '/api/v1/comments',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                feedId: feedId,
                writerId: feedWriterId,
                content: content
            }),
            success: (realId: number) => {
                $newItem.attr('data-comment-id', realId);
                $newItem.data('comment-id', realId);
            },
            error: () => {
                $newItem.remove();
                updateBadgeCount(-1);
                Show.error('댓글 작성에 실패했습니다.');
            }
        });
    });

    $btnMore.off('click').on('click', () => {
        if (hasNext && !isLoading) loadReplies();
    });

    $(document).off('click', '.btn-comment-delete').on('click', '.btn-comment-delete', function () {
        const $item = $(this).closest('.comment-item');
        const commentId = $item.data('comment-id');

        Confirm.open({
            title: '댓글 삭제',
            desc: '정말 삭제하시겠습니까?',
            actionText: '삭제',
            onConfirm: () => {
                if (String(commentId).startsWith('temp_')) {
                    $item.remove();
                    updateBadgeCount(-1);
                    return;
                }

                $.ajax({
                    url: `/api/v1/comments/${commentId}`,
                    type: 'DELETE',
                    success: () => {
                        $item.slideUp(200, function () { $(this).remove(); });
                        updateBadgeCount(-1);
                    },
                    error: () => Show.error("삭제 실패")
                });
            }
        });
    });

    $(document).off('click', '.btn-comment-edit').on('click', '.btn-comment-edit', function () {
        const $item = $(this).closest('.comment-item');
        const $wrapper = $item.find('.comment-content-wrapper');

        if ($item.find('.edit-input-box').length > 0) return;

        const originalText = ($wrapper.find('.comment-text').html() || '').replace(/<br\s*\/?>/gi, '\n');

        $wrapper.find('.comment-text').hide();
        $(this).hide();

        const editFormHtml = `
            <div class="edit-input-box" style="margin-top: 8px;">
                <textarea class="reply-textarea edit-textarea" rows="1">${originalText}</textarea>
                <div class="edit-actions" style="display: flex; gap: 8px; justify-content: flex-end; margin-top: 8px;">
                    <button type="button" class="btn-action btn-edit-cancel">취소</button>
                    <button type="button" class="btn-reply-submit btn-edit-save">저장</button>
                </div>
            </div>
        `;
        $wrapper.append(editFormHtml);
    });

    $(document).off('click', '.btn-edit-cancel').on('click', '.btn-edit-cancel', function () {
        const $wrapper = $(this).closest('.comment-content-wrapper');
        $wrapper.find('.edit-input-box').remove();
        $wrapper.find('.comment-text').show();
        $wrapper.closest('.comment-item').find('.btn-comment-edit').show();
    });

    $(document).off('click', '.btn-edit-save').on('click', '.btn-edit-save', function () {
        const $wrapper = $(this).closest('.comment-content-wrapper');
        const $item = $wrapper.closest('.comment-item');
        const commentId = $item.data('comment-id');
        const newContent = ($wrapper.find('.edit-textarea').val() as string).trim();

        if (!newContent) return Show.error("내용을 입력하세요");

        $.ajax({
            url: `/api/v1/comments/${commentId}`,
            type: 'PATCH',
            contentType: 'application/json',
            data: JSON.stringify({ content: newContent }),
            success: () => {
                $wrapper.find('.comment-text').html(newContent.replace(/\n/g, '<br>')).show();
                $wrapper.find('.edit-input-box').remove();
                $item.find('.btn-comment-edit').show();

                if ($item.find('.edited-badge').length === 0) {
                    $item.find('.comment-meta-actions .date').append('<span class="edited-badge">(수정됨)</span>');
                }
            },
            error: () => Show.error("수정 실패")
        });
    });

    // ===================================================================================
    //  SECTION: Event Bindings (Replies)
    // ===================================================================================

    $(document).off('click', '.btn-reply-toggle').on('click', '.btn-reply-toggle', function () {
        const $item = $(this).closest('.comment-item');

        if ($item.find('.re-reply-form').length > 0) {
            $item.find('.re-reply-form').remove();
            return;
        }

        const commentId = $item.data('comment-id');
        const formHtml = `
            <div class="re-reply-form">
                <div class="reply-input-box small">
                    <textarea class="reply-textarea re-reply-input" placeholder="답글 입력..." rows="1"></textarea>
                    <button type="button" class="btn-reply-submit btn-re-reply-write" data-parent-id="${commentId}" disabled>게시</button>
                </div>
            </div>
        `;
        $item.find('.comment-body').append(formHtml);
        $item.find('.re-reply-input').trigger('focus');
    });

    $(document).off('input', '.re-reply-input').on('input', '.re-reply-input', function (this: HTMLTextAreaElement) {
        const val = $(this).val() as string;
        $(this).next('.btn-re-reply-write').prop('disabled', val.trim().length === 0);
        this.style.height = 'auto';
        this.style.height = (this.scrollHeight) + 'px';
    });

    $(document).off('click', '.btn-re-reply-write').on('click', '.btn-re-reply-write', function () {
        const $btn = $(this);
        const $form = $btn.closest('.re-reply-form');
        const $input = $form.find('.re-reply-input');
        const $commentItem = $btn.closest('.comment-item');

        const parentCommentId = $btn.data('parent-id');
        const parentMemberId = $commentItem.data('member-id');
        const targetMemberId = $btn.data('target-member-id') || null;

        const rawContent = ($input.val() as string).trim();

        if (!rawContent) return;
        $btn.prop('disabled', true);

        const tempId = 'temp_reply_' + Date.now();
        const nowIso = new Date().toISOString();

        let displayTargetNickname: string | null = null;
        let cleanContent = rawContent;

        if (targetMemberId) {
            const match = rawContent.match(/^@(\S+)\s/);
            if (match) {
                displayTargetNickname = match[1];
                cleanContent = rawContent.replace(/^@\S+\s+/, '');
            }
        }

        const fakeReply: CommentData = {
            id: tempId,
            memberId: currentMemberId,
            memberNickname: myNickname,
            memberProfile: myProfilePath,
            targetMemberNickname: displayTargetNickname,
            content: cleanContent,
            createdTime: nowIso,
            updatedTime: nowIso
        };

        let $repliesWrapper = $commentItem.find('.replies-wrapper');
        if ($repliesWrapper.length === 0) {
            $repliesWrapper = $('<div class="replies-wrapper"></div>');
            $commentItem.find('.comment-body').append($repliesWrapper);
        }
        let $replyList = $repliesWrapper.find('.re-reply-list');
        if ($replyList.length === 0) {
            $replyList = $('<div class="re-reply-list" style="margin-top: 10px; padding-left: 20px;"></div>');
            $repliesWrapper.append($replyList);
        }

        $replyList.append(createReplyItem(fakeReply));
        $replyList.show();
        $form.remove();

        const $viewBtn = $repliesWrapper.find('.btn-view-replies');
        if ($viewBtn.length > 0) {
            const count = parseInt($viewBtn.text().replace(/[^0-9]/g, ''), 10) || 0;
            $viewBtn.text(`답글 ${count + 1}개 보기`);
        }

        $.ajax({
            url: '/api/v1/replies',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                commentId: parentCommentId,
                parentMemberId: parentMemberId,
                targetMemberId: targetMemberId,
                feedId: feedId,
                feedWriterId: feedWriterId,
                content: cleanContent
            }),
            success: (realId: number) => {
                const $newItem = $replyList.find(`[data-reply-id="${tempId}"]`);
                $newItem.attr('data-reply-id', realId);
                $newItem.data('reply-id', realId);
            },
            error: () => {
                $replyList.find(`[data-reply-id="${tempId}"]`).remove();
                Show.error("답글 작성 실패");
            }
        });
    });

    $(document).off('click', '.btn-nested-reply').on('click', '.btn-nested-reply', function () {
        const $replyItem = $(this).closest('.reply-item');
        const $commentItem = $(this).closest('.comment-item');

        const targetMemberId = $replyItem.data('member-id');
        const targetNickname = $replyItem.find('.writer').text();

        if ($commentItem.find('.re-reply-form').length === 0) {
            $commentItem.find('.btn-reply-toggle').trigger('click');
        }

        const $form = $commentItem.find('.re-reply-form');
        const $input = $form.find('.re-reply-input');
        const $submitBtn = $form.find('.btn-re-reply-write');

        $input.val(`@${targetNickname} `).trigger('focus');
        $submitBtn.data('target-member-id', targetMemberId);
    });

    $(document).off('click', '.btn-view-replies').on('click', '.btn-view-replies', function () {
        const $btn = $(this);
        const $wrapper = $btn.closest('.replies-wrapper');
        const commentId = $btn.closest('.comment-item').data('comment-id');

        if ($wrapper.find('.re-reply-list').length > 0) {
            $wrapper.find('.re-reply-list').toggle();
            return;
        }

        $btn.text('로딩 중...');

        $.ajax({
            url: '/api/v1/replies',
            type: 'GET',
            data: { commentId: commentId },
            success: (list: CommentData[]) => {
                const $listDiv = $('<div class="re-reply-list" style="margin-top: 10px; padding-left: 20px;"></div>');
                if (list && list.length > 0) {
                    list.forEach(r => $listDiv.append(createReplyItem(r)));
                }
                $wrapper.append($listDiv);
                $btn.hide();
            },
            error: () => Show.error('대댓글 로드 실패')
        });
    });

    $(document).off('click', '.btn-reply-edit').on('click', '.btn-reply-edit', function () {
        const $item = $(this).closest('.reply-item');
        const $wrapper = $item.find('.reply-content-wrapper');
        if ($item.find('.edit-input-box').length > 0) return;

        const $clone = $wrapper.find('.reply-text').clone();
        $clone.find('.mention-target').remove();
        const originalText = ($clone.html() || '').replace(/<br\s*\/?>/gi, '\n').trim();

        $wrapper.find('.reply-text').hide();
        $(this).hide();

        const editHtml = `
            <div class="edit-input-box" style="margin-top: 8px;">
                <textarea class="reply-textarea edit-reply-textarea" rows="1">${originalText}</textarea>
                <div class="edit-actions" style="display: flex; gap: 8px; justify-content: flex-end; margin-top: 8px;">
                    <button type="button" class="btn-action btn-reply-edit-cancel">취소</button>
                    <button type="button" class="btn-reply-submit btn-reply-edit-save">저장</button>
                </div>
            </div>
        `;
        $wrapper.append(editHtml);
    });

    $(document).off('click', '.btn-reply-edit-cancel').on('click', '.btn-reply-edit-cancel', function () {
        const $wrapper = $(this).closest('.reply-content-wrapper');
        $wrapper.find('.edit-input-box').remove();
        $wrapper.find('.reply-text').show();
        $wrapper.closest('.reply-item').find('.btn-reply-edit').show();
    });

    $(document).off('click', '.btn-reply-edit-save').on('click', '.btn-reply-edit-save', function () {
        const $wrapper = $(this).closest('.reply-content-wrapper');
        const $item = $wrapper.closest('.reply-item');
        const replyId = $item.data('reply-id');
        const newContent = ($wrapper.find('.edit-reply-textarea').val() as string).trim();

        if (!newContent) return Show.error("내용 입력 필요");

        $.ajax({
            url: `/api/v1/replies/${replyId}`,
            type: 'PATCH',
            contentType: 'application/json',
            data: JSON.stringify({ content: newContent }),
            success: () => {
                const $textSpan = $wrapper.find('.reply-text');
                const $mention = $textSpan.find('.mention-target');

                let html = newContent.replace(/\n/g, '<br>');
                if ($mention.length > 0) html = $mention.prop('outerHTML') + ' ' + html;

                $textSpan.html(html).show();
                $wrapper.find('.edit-input-box').remove();
                $item.find('.btn-reply-edit').show();

                if ($item.find('.edited-badge').length === 0) {
                    $item.find('.reply-meta-actions .date').append('<span class="edited-badge">(수정됨)</span>');
                }
            },
            error: () => Show.error("수정 실패")
        });
    });

    $(document).off('click', '.btn-reply-delete').on('click', '.btn-reply-delete', function () {
        const $item = $(this).closest('.reply-item');
        const replyId = $item.data('reply-id');

        Confirm.open({
            title: '답글 삭제',
            desc: '정말 삭제하시겠습니까?',
            actionText: '삭제',
            onConfirm: () => {
                if (String(replyId).startsWith('temp_')) {
                    $item.remove();
                    return;
                }

                $.ajax({
                    url: `/api/v1/replies/${replyId}`,
                    type: 'DELETE',
                    success: () => {
                        const $repliesWrapper = $item.closest('.replies-wrapper');
                        $item.slideUp(200, function () {
                            $(this).remove();
                            const $viewBtn = $repliesWrapper.find('.btn-view-replies');
                            if($viewBtn.length > 0) {
                                const count = parseInt($viewBtn.text().replace(/[^0-9]/g, ''), 10) || 0;
                                if(count > 0) $viewBtn.text(`답글 ${count - 1}개 보기`);
                            }
                        });
                    },
                    error: () => Show.error("삭제 실패")
                });
            }
        });
    });

    $(document).off('input', '.edit-textarea, .edit-reply-textarea').on('input', '.edit-textarea, .edit-reply-textarea', function (this: HTMLTextAreaElement) {
        this.style.height = 'auto';
        this.style.height = (this.scrollHeight) + 'px';
    });

    // Initialize System
    loadReplies();
}