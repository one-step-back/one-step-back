package com.app.onestepback.domain.dto.follow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowSearchCond {
    private Long memberId;
    private Sort sort;

    public enum Sort {
        LATEST, NAME, BLOG
    }
}