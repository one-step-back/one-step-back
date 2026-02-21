package com.app.onestepback.web.api.v1.member.request;

import com.app.onestepback.global.annotation.MaxFileSize;
import com.app.onestepback.service.member.cmd.MemberRegisterCmd;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.multipart.MultipartFile;

import static com.app.onestepback.global.util.Stringx.trimToNull;

public record MemberRegisterRequest(
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~20자여야 합니다.")
        String rawPassword,

        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$", message = "닉네임은 특수문자를 제외한 2~10자여야 합니다.")
        String nickname,

        // 5MB 제한 적용
        @MaxFileSize(max = 5, message = "프로필 이미지는 5MB 이하여야 합니다.")
        MultipartFile profileImage
) {
    public MemberRegisterRequest {
        email = trimToNull(email);
        nickname = trimToNull(nickname);
    }

    public MemberRegisterCmd toCmd() {
        return new MemberRegisterCmd(
                this.email,
                this.rawPassword,
                this.nickname,
                null,
                this.profileImage
        );
    }
}