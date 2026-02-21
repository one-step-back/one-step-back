package com.app.onestepback.service.follow;

import com.app.onestepback.domain.dto.follow.FollowDTO;
import com.app.onestepback.domain.dto.follow.FollowSearchCond;
import com.app.onestepback.repository.FollowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 아티스트 구독(Follow) 비즈니스 로직을 처리하는 서비스 구현체입니다.
 * <p>
 * 읽기 및 쓰기 작업의 특성에 맞추어 세밀한 트랜잭션 관리를 수행하며,
 * 데이터 정합성을 유지하기 위해 캐시 무효화(Evict) 전략을 동반합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowMapper followMapper;

    /**
     * 아티스트 구독 상태를 데이터베이스에 반영합니다.
     * <p>
     * 아티스트의 실제 총 구독자 수는 데이터베이스 내부의 트리거를 통해 자동으로 동기화되므로,
     * 본 메서드에서는 교차 테이블의 레코드 삽입 및 삭제 작업만을 수행합니다.
     * 다중 클릭으로 인한 중복 키 예외(DuplicateKeyException)는 멱등성 보장을 위해 내부적으로 무시됩니다.
     * </p>
     *
     * @param artistId 대상 아티스트의 고유 식별자
     * @param memberId 요청 회원의 고유 식별자
     * @param status   구독 활성화 여부
     */
    @Override
    @Transactional
    @CacheEvict(value = "artistDetail", key = "#artistId + '_' + #memberId")
    public void setSubscription(Long artistId, Long memberId, boolean status) {
        if (status) {
            try {
                followMapper.insert(artistId, memberId);
            } catch (DuplicateKeyException ignored) {
                /* 클라이언트의 다중 요청으로 인한 중복 발생 시 예외를 무시하여 시스템의 멱등성을 보장합니다. */
            }
        } else {
            followMapper.delete(artistId, memberId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findSubscriberIds(long artistId) {
        return followMapper.findFollowerIdsByArtistId(artistId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FollowDTO.Following> getFollowings(FollowSearchCond cond) {
        return followMapper.selectFollowingByMemberId(cond);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FollowDTO.Follower> getArtistFollowers(Long artistId) {
        return followMapper.selectFollowersByArtistId(artistId);
    }
}