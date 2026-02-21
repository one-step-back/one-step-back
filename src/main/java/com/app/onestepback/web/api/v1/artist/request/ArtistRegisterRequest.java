package com.app.onestepback.web.api.v1.artist.request;

import com.app.onestepback.global.annotation.MaxFileSize;
import com.app.onestepback.service.artist.cmd.ArtistRegisterCMD;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import static com.app.onestepback.global.util.Stringx.trimToNull;

public record ArtistRegisterRequest(
        @NotBlank(message = "블로그 이름은 필수입니다.")
        @Size(min = 2, max = 20, message = "블로그 이름은 2~20자 사이여야 합니다.")
        String blogName,

        @NotBlank(message = "블로그 소개는 필수입니다.")
        @Size(max = 1000, message = "소개글은 1000자 이내로 작성해주세요.")
        String description,

        @MaxFileSize(max = 10, message = "이미지 크기는 10MB 이내여야 합니다.")
        MultipartFile blogImage
) {
    public ArtistRegisterRequest {
        blogName = trimToNull(blogName);
        description = trimToNull(description);
    }

    public ArtistRegisterCMD toCmd(Long memberId) {
        return new ArtistRegisterCMD(
                memberId,
                blogName,
                description,
                blogImage
        );
    }
}