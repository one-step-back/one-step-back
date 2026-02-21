package com.app.onestepback.service.feed;

import com.app.onestepback.repository.FeedLikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 피드 좋아요 상태 변경을 처리하는 서비스 구현체입니다.
 */
@Service
@RequiredArgsConstructor
public class FeedLikeServiceImpl implements FeedLikeService {

    private final FeedLikeMapper feedLikeMapper;

    @Override
    @Transactional
    public void setLike(Long feedId, Long memberId, boolean isLike) {
        if (isLike) {
            try {
                feedLikeMapper.insert(feedId, memberId);
            } catch (DuplicateKeyException ignored) {
                /* 클라이언트의 다중 요청으로 인한 중복 키 발생 시 예외를 무시하여 안정적인 멱등성을 유지합니다. */
            }
        } else {
            feedLikeMapper.delete(feedId, memberId);
        }
    }
}