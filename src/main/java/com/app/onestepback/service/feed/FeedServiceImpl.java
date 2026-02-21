package com.app.onestepback.service.feed;

import com.app.onestepback.domain.dto.feed.FeedDetailDTO;
import com.app.onestepback.domain.dto.feed.FeedListDTO;
import com.app.onestepback.domain.dto.feed.FeedSearchCond;
import com.app.onestepback.domain.model.FeedVO;
import com.app.onestepback.domain.type.feed.FeedMediaType;
import com.app.onestepback.domain.type.feed.FeedStatus;
import com.app.onestepback.domain.type.notification.NotificationType;
import com.app.onestepback.global.common.Slice;
import com.app.onestepback.global.exception.BusinessException;
import com.app.onestepback.global.exception.ErrorCode;
import com.app.onestepback.repository.FeedMapper;
import com.app.onestepback.service.feed.cmd.FeedModifyCmd;
import com.app.onestepback.service.feed.cmd.FeedWriteCmd;
import com.app.onestepback.service.file.FileInfo;
import com.app.onestepback.service.file.FileService;
import com.app.onestepback.service.file.MediaUploadUseCase;
import com.app.onestepback.service.follow.FollowService;
import com.app.onestepback.service.notification.event.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 피드(Feed) 비즈니스 로직을 처리하는 핵심 서비스 구현체입니다.
 * <p>
 * 클래스 레벨의 트랜잭션을 제거하고 각 메서드의 특성(조회/수정)에 맞게
 * 세밀한 트랜잭션 격리 수준을 유지합니다.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FileService fileService;
    private final MediaUploadUseCase mediaService;
    private final FollowService followService;
    private final NotificationPublisher notificationPublisher;

    private final FeedMapper feedMapper;

    @Override
    @Transactional
    public void write(FeedWriteCmd cmd) {
        FeedMediaType mediaType = decideMediaTypeAndProcess(cmd.fileIds());
        String title = generateTitle(cmd.title(), cmd.content());

        int refinedTier = 0;
        if (cmd.status() == FeedStatus.MEMBER_ONLY) {
            refinedTier = (cmd.minTier() == null || cmd.minTier() < 1) ? 1 : cmd.minTier();
        }

        FeedVO feedVO = FeedVO.builder()
                .artistId(cmd.artistId())
                .feedTitle(title)
                .feedContent(cmd.content())
                .feedCategory(cmd.category())
                .feedStatus(cmd.status())
                .feedMinTier(refinedTier)
                .feedMediaType(mediaType)
                .build();
        feedMapper.insertFeed(feedVO);

        if (cmd.fileIds() != null && !cmd.fileIds().isEmpty()) {
            fileService.connectFiles(
                    feedVO.getId(),
                    "FEED",
                    cmd.fileIds(),
                    cmd.artistId()
            );
        }

        List<Long> subscriberIds = followService.findSubscriberIds(cmd.artistId());
        String url = "/artist/" + cmd.artistId() + "/feed/view?id=" + feedVO.getId();
        notificationPublisher.sendBroadcast(cmd.artistId(), subscriberIds, NotificationType.NEW_FEED, url);
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<FeedListDTO> getList(FeedSearchCond cond) {
        cond.setOwner(cond.getArtistId() != null && cond.getArtistId().equals(cond.getViewerId()));
        List<FeedListDTO> result = feedMapper.selectFeedList(cond);
        return new Slice<>(result, cond.getSize());
    }

    @Override
    @Transactional
    public FeedDetailDTO getDetail(long feedId, long artistId, Long viewerId) {
        FeedDetailDTO feed = feedMapper.selectFeedDetail(feedId, artistId, viewerId).orElseThrow(
                () -> new BusinessException(ErrorCode.FEED_NOT_FOUND)
        );

        feedMapper.updateViewCount(feedId);
        return feed;
    }

    @Override
    @Transactional(readOnly = true)
    public FeedDetailDTO getForEdit(long feedId, long artistId) {
        return feedMapper.selectFeedDetail(feedId, artistId, artistId).orElseThrow(
                () -> new BusinessException(ErrorCode.FEED_NOT_FOUND)
        );
    }

    @Override
    @Transactional
    public void modify(FeedModifyCmd cmd) {
        FeedMediaType mediaType = decideMediaTypeAndProcess(cmd.fileIds());
        String title = generateTitle(cmd.title(), cmd.content());

        FeedVO feedVO = FeedVO.builder()
                .id(cmd.feedId())
                .artistId(cmd.artistId())
                .feedTitle(title)
                .feedContent(cmd.content())
                .feedCategory(cmd.category())
                .feedStatus(cmd.status())
                .feedMediaType(mediaType)
                .build();

        feedMapper.updateFeed(feedVO);

        if (cmd.deleteFileIds() != null && !cmd.deleteFileIds().isEmpty()) {
            fileService.disconnectFiles(cmd.deleteFileIds(), cmd.artistId());
        }

        if (cmd.fileIds() != null && !cmd.fileIds().isEmpty()) {
            fileService.connectFiles(
                    cmd.feedId(),
                    "FEED",
                    cmd.fileIds(),
                    cmd.artistId()
            );
        }
    }

    @Override
    @Transactional
    public void softDelete(long feedId, long artistId) {
        int deletedCount = feedMapper.softDeleteByFeedIdAndArtistId(feedId, artistId);

        if (deletedCount == 0) {
            throw new BusinessException(ErrorCode.FEED_DELETE_NOT_ALLOWED);
        }
    }

    /**
     * 피드 제목이 비어있을 경우 본문 내용을 기반으로 제목을 자동 생성합니다.
     */
    private String generateTitle(String title, String content) {
        if (title != null && !title.isBlank()) {
            return title;
        }
        String sanitizedContent = content.replaceAll("[\\r\\n]+", " ");
        return sanitizedContent.length() > 20
                ? sanitizedContent.substring(0, 20) + "..."
                : sanitizedContent;
    }

    /**
     * 첨부된 파일의 확장자를 분석하여 피드의 미디어 타입(사진/영상/혼합)을 결정하고,
     * 이미지일 경우 비동기 모자이크 처리를 트리거합니다.
     */
    private FeedMediaType decideMediaTypeAndProcess(List<String> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return FeedMediaType.TEXT;
        }

        boolean hasImage = false;
        boolean hasVideo = false;

        for (String fileId : fileIds) {
            FileInfo fileInfo = fileService.getFile(fileId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));

            String ext = fileInfo.fileExtension().toLowerCase();

            if (isVideoExtension(ext)) {
                hasVideo = true;
            } else {
                hasImage = true;
                mediaService.process(fileId);
            }
        }

        if (hasImage && hasVideo) return FeedMediaType.MIXED;
        if (hasVideo) return FeedMediaType.VIDEO;
        return FeedMediaType.IMAGE;
    }

    /**
     * 주어진 확장자가 비디오 파일인지 여부를 검증합니다.
     */
    private boolean isVideoExtension(String extension) {
        return switch (extension) {
            case "mp4", "mov", "avi", "wmv", "mkv", "webm" -> true;
            default -> false;
        };
    }
}