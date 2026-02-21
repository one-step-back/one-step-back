package com.app.onestepback.web.api.v1.feed.request;

import com.app.onestepback.domain.type.feed.FeedCategory;
import com.app.onestepback.domain.type.feed.FeedStatus;
import com.app.onestepback.service.feed.cmd.FeedWriteCmd;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

import static com.app.onestepback.global.util.Stringx.trimToNull;

public record FeedWriteRequest(
        @Size(max = 100, message = "제목은 100자 이내로 입력해주세요.")
        String title,

        @Size(max = 4000, message = "내용은 4000자 이내로 입력해주세요.")
        String content,

        @NotNull(message = "카테고리를 선택해주세요.")
        FeedCategory category,

        @NotNull(message = "공개 상태를 설정해주세요.")
        FeedStatus status,

        @Max(value = 3, message = "최대 티어는 3 이하이어야 합니다.")
        Integer minTier,

        List<String> fileIds
) {
    public FeedWriteRequest {
        title = trimToNull(title);
        content = trimToNull(content);
    }

    public FeedWriteCmd toCmd(long artistId) {
        return new FeedWriteCmd(
                artistId,
                title,
                content,
                category,
                status,
                minTier, // Client가 요청한 값 전달 (Null일 경우 Service에서 0 또는 1로 처리)
                fileIds
        );
    }
}