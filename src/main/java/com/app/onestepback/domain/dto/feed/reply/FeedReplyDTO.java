package com.app.onestepback.domain.dto.feed.reply;

import com.app.onestepback.global.util.FileUrlSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FeedReplyDTO {
    private Long id;
    private Long commentId;
    private Long memberId;
    private Long targetMemberId; // 멘션 대상 ID (Nullable)
    
    private String content;
    
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    // 작성자 정보
    private String memberNickname;
    @JsonSerialize(using = FileUrlSerializer.class)
    private String memberProfile; // 프로필 경로 (URL 처리됨)

    // 멘션 대상 정보 (Target이 있을 경우)
    private String targetMemberNickname;
}