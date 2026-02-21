package com.app.onestepback.domain.dto.follow;

import com.app.onestepback.global.util.FileUrlSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FollowDTO {

    public record Following(
            Long id,
            String artistNickname,
            @JsonSerialize(using = FileUrlSerializer.class)
            String artistProfilePath,
            String blogName,
            LocalDateTime followTime
    ) {
    }

    public record Follower(
            Long id,
            String artistNickname,
            @JsonSerialize(using = FileUrlSerializer.class)
            String artistProfilePath,
            LocalDateTime followTime
    ) {
    }
}
