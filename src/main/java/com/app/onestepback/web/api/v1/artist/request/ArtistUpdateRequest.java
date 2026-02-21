package com.app.onestepback.web.api.v1.artist.request;

import com.app.onestepback.global.annotation.MaxFileSize;
import com.app.onestepback.service.artist.cmd.ArtistUpdateCmd;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import static com.app.onestepback.global.util.Stringx.trimToNull;

public record ArtistUpdateRequest(
        @NotBlank(message = "블로그 이름은 필수입니다.")
        @Size(min = 2, max = 20, message = "블로그 이름은 2~20자 사이여야 합니다.")
        String blogName,
        @NotBlank(message = "블로그 소개는 필수입니다.")
        @Size(max = 1000, message = "소개글은 1000자 이내로 작성해주세요.")
        String description,
        @MaxFileSize(max = 10, message = "블로그 배너는 크기는 10MB 이내여야 합니다.")
        MultipartFile blogImage,

        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$", message = "닉네임은 특수문자를 제외한 2~10자여야 합니다.")
        String memberNickname,

        @MaxFileSize(max = 5, message = "프로필 이미지는 5MB 이하여야 합니다.")
        MultipartFile profileImage
) {
    public ArtistUpdateRequest {
        blogName = trimToNull(blogName);
        description = trimToNull(description);
        memberNickname = trimToNull(memberNickname);
    }

    public ArtistUpdateCmd toCmd(long memberId) {
        return new ArtistUpdateCmd(
                memberId,
                blogName,
                description,
                blogImage,
                memberNickname,
                profileImage
        );
    }
}
