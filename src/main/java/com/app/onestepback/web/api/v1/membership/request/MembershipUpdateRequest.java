package com.app.onestepback.web.api.v1.membership.request;

import com.app.onestepback.service.membership.cmd.MembershipUpdateCmd;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import static com.app.onestepback.global.util.Stringx.trimToNull;

public record MembershipUpdateRequest(
        @NotBlank(message = "멤버십 이름은 필수입니다.")
        @Length(max = 20, message = "멤버십 이름은 20자를 초과할 수 없습니다.")
        String name,

        @NotBlank(message = "혜택 설명은 필수입니다.")
        @Length(max = 500, message = "혜택 설명은 500자를 초과할 수 없습니다.")
        String description,

        @NotNull(message = "월간 가격은 필수입니다.")
        @Min(value = 1000, message = "가격은 최소 1,000원 이상이어야 합니다.")
        @Max(value = 10000000, message = "가격이 너무 높습니다.")
        Long price,

        @NotNull(message = "등급 정보가 누락되었습니다.")
        @Min(value = 1, message = "등급은 1 이상이어야 합니다.")
        @Max(value = 3, message = "등급은 3 이하여야 합니다.")
        Integer levelOrder,

        // --- 이미지 처리 필드 (Image Handling) ---
        String currentImageId, // 기존 이미지 ID (유지 판단용)
        String newImageId,     // 새로 업로드된 이미지 ID (교체용)
        boolean deleteImage    // 기존 이미지 삭제 여부
) {
    public MembershipUpdateRequest {
        name = trimToNull(name);
        description = trimToNull(description);
    }

    // Command 변환: ID들은 외부(Path, Session)에서 주입받는다.
    public MembershipUpdateCmd toCmd(Long membershipId, Long artistId) {
        return new MembershipUpdateCmd(
                membershipId,
                artistId,
                name,
                description,
                currentImageId,
                newImageId,
                deleteImage,
                price,
                levelOrder
        );
    }
}