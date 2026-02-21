package com.app.onestepback.domain.dto.artist;

import com.app.onestepback.domain.type.bank.BankCode;
import com.app.onestepback.global.util.FileUrlSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ArtistDTO {
    public record BankInfo(
            Long id,
            String memberStatus,
            BankCode bankName,
            String accountNumber,
            String accountHolder
    ) {
    }

    public record ShortInfo(
            Long id,
            String nickname,
            @JsonSerialize(using = FileUrlSerializer.class)
            String profilePath,
            String blogName
    ) {
    }

    public record MyArtists(
            List<ShortInfo> subscriptions,
            List<ShortInfo> follows
    ) {
    }

    public record ListInfo(
            Long id,
            String nickname,
            String blogName,
            @JsonSerialize(using = FileUrlSerializer.class)
            String profilePath,
            @JsonSerialize(using = FileUrlSerializer.class)
            String blogImgPath,
            Long followerCount,
            Long feedCount,
            boolean isFollowing
    ) {
    }
}