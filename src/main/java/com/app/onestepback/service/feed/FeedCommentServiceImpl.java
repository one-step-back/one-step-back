package com.app.onestepback.service.feed;

import com.app.onestepback.domain.dto.feed.comment.FeedCommentDTO;
import com.app.onestepback.domain.dto.feed.comment.FeedCommentSearchCond;
import com.app.onestepback.domain.model.FeedCommentVO;
import com.app.onestepback.domain.type.notification.NotificationType;
import com.app.onestepback.global.common.Slice;
import com.app.onestepback.repository.FeedCommentMapper;
import com.app.onestepback.service.feed.cmd.FeedCommentWriteCmd;
import com.app.onestepback.service.notification.event.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 피드 댓글(Comment) 비즈니스 로직을 처리하는 서비스 구현체입니다.
 */
@Service
@RequiredArgsConstructor
public class FeedCommentServiceImpl implements FeedCommentService {

    private final FeedCommentMapper feedCommentMapper;
    private final NotificationPublisher notificationPublisher;

    @Override
    @Transactional
    public Long write(FeedCommentWriteCmd cmd) {
        FeedCommentVO feedCommentVO = FeedCommentVO.builder()
                .feedId(cmd.feedId())
                .memberId(cmd.memberId())
                .content(cmd.content())
                .build();

        feedCommentMapper.insert(feedCommentVO);

        if (!Objects.equals(cmd.writerId(), cmd.memberId())) {
            String url = "/artist/" + cmd.writerId() + "/feed/view?id=" + cmd.feedId();
            notificationPublisher.send(cmd.memberId(), cmd.writerId(), NotificationType.NEW_COMMENT, url);
        }

        return feedCommentVO.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<FeedCommentDTO> getSlice(FeedCommentSearchCond cond) {
        List<FeedCommentDTO> content = feedCommentMapper.selectSlice(cond.getFeedId(), cond.getLastCommentId(), cond.getSize());
        return new Slice<>(content, cond.getSize());
    }

    @Override
    @Transactional
    public void modify(Long commentId, Long memberId, String content) {
        feedCommentMapper.update(commentId, memberId, content);
    }

    @Override
    @Transactional
    public void softDelete(Long commentId, Long memberId) {
        feedCommentMapper.softDelete(commentId, memberId);
    }
}