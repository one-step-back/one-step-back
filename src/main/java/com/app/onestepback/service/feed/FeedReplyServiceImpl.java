package com.app.onestepback.service.feed;

import com.app.onestepback.domain.dto.feed.reply.FeedReplyDTO;
import com.app.onestepback.domain.model.FeedReplyVO;
import com.app.onestepback.domain.type.notification.NotificationType;
import com.app.onestepback.repository.FeedReplyMapper;
import com.app.onestepback.service.feed.cmd.FeedReplyWriteCmd;
import com.app.onestepback.service.notification.event.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 피드 답글(Reply/대댓글) 비즈니스 로직을 처리하는 서비스 구현체입니다.
 */
@Service
@RequiredArgsConstructor
public class FeedReplyServiceImpl implements FeedReplyService {

    private final FeedReplyMapper feedReplyMapper;
    private final NotificationPublisher notificationPublisher;

    @Override
    @Transactional
    public Long write(FeedReplyWriteCmd cmd) {
        FeedReplyVO replyVO = FeedReplyVO.builder()
                .commentId(cmd.commentId())
                .memberId(cmd.memberId())
                .targetMemberId(cmd.targetMemberId())
                .content(cmd.content())
                .build();

        feedReplyMapper.insert(replyVO);

        /* 특정 회원을 멘션한 경우 해당 회원에게, 그렇지 않은 경우 부모 댓글 작성자에게 알림을 발송합니다. */
        Long receiverId = (cmd.targetMemberId() != null)
                ? cmd.targetMemberId()
                : cmd.parentMemberId();

        if (!receiverId.equals(cmd.memberId())) {
            String url = "/artist/" + cmd.feedWriterId() + "/feed/view?id=" + cmd.feedId();
            notificationPublisher.send(cmd.memberId(), receiverId, NotificationType.NEW_REPLY, url);
        }

        return replyVO.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedReplyDTO> getList(Long commentId) {
        return feedReplyMapper.selectAll(commentId);
    }

    @Override
    @Transactional
    public void modify(Long replyId, Long memberId, String content) {
        feedReplyMapper.update(replyId, memberId, content);
    }

    @Override
    @Transactional
    public void delete(Long replyId, Long memberId) {
        feedReplyMapper.softDelete(replyId, memberId);
    }
}