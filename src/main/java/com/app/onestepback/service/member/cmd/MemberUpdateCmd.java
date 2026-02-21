package com.app.onestepback.service.member.cmd;

import org.springframework.web.multipart.MultipartFile;

public record MemberUpdateCmd(
        long memberId,
        String nickname,
        String introduction,
        MultipartFile profileImage
) {
}
