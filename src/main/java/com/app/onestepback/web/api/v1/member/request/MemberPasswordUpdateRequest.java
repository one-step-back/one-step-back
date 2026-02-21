package com.app.onestepback.web.api.v1.member.request;

import com.app.onestepback.service.member.cmd.PasswordUpdateCmd;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static com.app.onestepback.global.util.Stringx.trimToNull;

public record MemberPasswordUpdateRequest(
        String currentPassword,

        @NotBlank(message = "새 비밀번호는 필수 입력값입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~20자여야 합니다.")
        String newPassword
) {
    public MemberPasswordUpdateRequest {
        currentPassword = trimToNull(currentPassword);
        newPassword = trimToNull(newPassword);
    }

    public PasswordUpdateCmd toCmd(Long memberId) {
        return new PasswordUpdateCmd(
                memberId,
                currentPassword,
                newPassword
        );
    }
}