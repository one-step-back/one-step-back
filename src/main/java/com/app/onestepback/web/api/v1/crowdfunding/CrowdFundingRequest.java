package com.app.onestepback.web.api.v1.crowdfunding;

import com.app.onestepback.service.crowdfunding.CrowdFundingServiceCommand;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

import static com.app.onestepback.global.util.Stringx.trimToNull;

public class CrowdFundingRequest {

    public record Create(
            @NotNull(message = "아티스트 정보는 필수입니다.")
            Long artistId,

            @NotBlank(message = "제목을 입력해주세요.")
            String title,

            @NotBlank(message = "내용을 입력해주세요.")
            String content,

            @NotNull(message = "목표 금액을 설정해주세요.")
            @Min(value = 1000, message = "최소 목표 금액은 1,000원입니다.")
            Long targetAmount,

            String fileId,

            @FutureOrPresent(message = "시작일은 현재 날짜 이후여야 합니다.")
            LocalDate startDate,

            @Future(message = "종료일은 현재 날짜 이후여야 합니다.")
            LocalDate endDate
    ) {
        public Create {
            title = trimToNull(title);
            content = trimToNull(content);
            fileId = trimToNull(fileId);
        }

        public CrowdFundingServiceCommand.Create toCommand(Long writerId) {
            return new CrowdFundingServiceCommand.Create(
                    artistId,
                    writerId,
                    title,
                    content,
                    targetAmount,
                    fileId,
                    startDate != null ? startDate.atStartOfDay() : null,
                    endDate != null ? endDate.atTime(23, 59, 59) : null
            );
        }
    }

    public record EditAccept(
            @NotBlank(message = "제목을 입력해주세요.")
            String title,

            @NotBlank(message = "내용을 입력해주세요.")
            String content,

            @NotNull(message = "목표 금액을 설정해주세요.")
            @Min(value = 1000, message = "최소 목표 금액은 1,000원입니다.")
            Long targetAmount,

            String fileId,

            @NotNull(message = "시작일은 필수입니다.")
            @FutureOrPresent(message = "시작일은 현재 날짜 이후여야 합니다.")
            LocalDate startDate,

            @NotNull(message = "종료일은 필수입니다.")
            @Future(message = "종료일은 현재 날짜 이후여야 합니다.")
            LocalDate endDate
    ) {
        public EditAccept {
            title = trimToNull(title);
            content = trimToNull(content);
            fileId = trimToNull(fileId);
        }

        public CrowdFundingServiceCommand.EditAccept toCommand(Long fundingId, Long artistId) {
            return new CrowdFundingServiceCommand.EditAccept(
                    fundingId,
                    artistId,
                    title,
                    content,
                    targetAmount,
                    fileId,
                    startDate.atStartOfDay(),
                    endDate.atTime(23, 59, 59)
            );
        }
    }

    public record PaymentRequest(
            @NotNull Long paymentMethodId,
            @NotNull @Min(1000) Long amount
    ) {
    }

    public record Reject(
            @NotBlank(message = "반려 사유는 필수입니다.")
            String rejectReason
    ) {
    }
}