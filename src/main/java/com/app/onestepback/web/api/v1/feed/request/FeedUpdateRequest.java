package com.app.onestepback.web.api.v1.feed.request;

import com.app.onestepback.domain.type.feed.FeedCategory;
import com.app.onestepback.domain.type.feed.FeedStatus;
import com.app.onestepback.service.feed.cmd.FeedModifyCmd;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

import static com.app.onestepback.global.util.Stringx.trimToNull;

public record FeedUpdateRequest(
        @Size(max = 100, message = "제목은 100자 이내로 입력해주세요.")
        String title,

        @Size(max = 4000, message = "내용은 4000자 이내로 입력해주세요.")
        String content,

        @NotNull(message = "카테고리를 선택해주세요.")
        FeedCategory category,

        @NotNull(message = "공개 상태를 설정해주세요.")
        FeedStatus status,

        List<String> deleteFileIds,

        List<String> fileIds
) {
    public FeedUpdateRequest {
        title = trimToNull(title);
        content = trimToNull(content);
    }

    public FeedModifyCmd toCmd(long feedId, long artistId) {
        return new FeedModifyCmd(
                feedId,
                artistId,
                title,
                content,
                category,
                status,
                fileIds,
                deleteFileIds
        );
    }
}