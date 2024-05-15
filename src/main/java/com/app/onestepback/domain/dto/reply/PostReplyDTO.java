package com.app.onestepback.domain.dto.reply;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PostReplyDTO {
    private Long replyId;
    private Long memberId;
    private String content;
    private LocalDateTime createdTime;
    private String nickname;
    private String kakaoProfileUrl;
    private String profileName;
    private String profilePath;

    @Builder
    public PostReplyDTO(Long replyId, Long memberId, String content, LocalDateTime createdTime, String nickname, String kakaoProfileUrl, String profileName, String profilePath) {
        this.replyId = replyId;
        this.memberId = memberId;
        this.content = content;
        this.createdTime = createdTime;
        this.nickname = nickname;
        this.kakaoProfileUrl = kakaoProfileUrl;
        this.profileName = profileName;
        this.profilePath = profilePath;
    }
}
