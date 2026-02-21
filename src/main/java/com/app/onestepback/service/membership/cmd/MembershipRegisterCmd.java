package com.app.onestepback.service.membership.cmd;

import lombok.Builder;

@Builder
public record MembershipRegisterCmd(
        Long artistId,
        String name,
        String description,
        String imageId,     // API로 업로드 후 받은 UUID
        Long price,
        Integer levelOrder  // 1, 2, 3 중 하나
) {
}