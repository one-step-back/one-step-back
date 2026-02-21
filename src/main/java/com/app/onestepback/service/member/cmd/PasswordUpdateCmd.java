package com.app.onestepback.service.member.cmd;

public record PasswordUpdateCmd(
        long memberId,
        String currentPassword,
        String newPassword
) {
}
