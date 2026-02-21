package com.app.onestepback.web.api.v1.member.request;

import com.app.onestepback.global.annotation.MaxFileSize;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import static com.app.onestepback.global.util.Stringx.trimToNull;

public record MemberUpdateRequest(
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$", message = "닉네임은 특수문자를 제외한 2~10자여야 합니다.")
        String nickname,
        @Size(max = 500, message = "소개글은 최대 500자까지 입력 가능합니다.")
        String introduction,
        @MaxFileSize(max = 5, message = "프로필 이미지는 5MB 이하여야 합니다.")
        MultipartFile profileImage
) {
    public MemberUpdateRequest {
        nickname = trimToNull(nickname);
        introduction = trimToNull(introduction);
    }
}