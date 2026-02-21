package com.app.onestepback.service.crowdfunding;

import java.time.LocalDateTime;
import java.util.Objects;

public class CrowdFundingServiceCommand {
    public record Create(
            Long artistId,
            Long writerId,
            String title,
            String content,
            Long targetAmount,
            String fileId,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        public boolean isWriterArtist() {
            return Objects.equals(writerId, artistId);
        }
    }

    public record EditAccept(
            Long fundingId,
            Long artistId,
            String title,
            String content,
            Long targetAmount,
            String fileId,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
    }
}