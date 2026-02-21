package com.app.onestepback.service.membership.cmd;

import lombok.Builder;

@Builder
public record MembershipSubscribeCmd(
        Long memberId,
        Long artistId,
        Long targetMembershipId
) {
}