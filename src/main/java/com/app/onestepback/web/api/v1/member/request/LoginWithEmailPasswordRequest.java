package com.app.onestepback.web.api.v1.member.request;

import com.app.onestepback.service.member.cmd.LoginWithEmailPasswordCmd;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import static com.app.onestepback.global.util.Stringx.trimToNull;

public record LoginWithEmailPasswordRequest(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password
) {
    /**
     * Compact Constructor
     * 이메일은 공백 제거, 비밀번호는 원본 유지
     */
    public LoginWithEmailPasswordRequest {
        email = trimToNull(email);
    }

    public LoginWithEmailPasswordCmd toCmd() {
        return new LoginWithEmailPasswordCmd(
                this.email,
                this.password
        );
    }
}