package com.app.onestepback.service.member.cmd;

import org.springframework.web.multipart.MultipartFile;

public record MemberRegisterCmd(
        String email,
        String rawPassword,
        String nickname,
        String introduction,
        MultipartFile profileImage
) {
}