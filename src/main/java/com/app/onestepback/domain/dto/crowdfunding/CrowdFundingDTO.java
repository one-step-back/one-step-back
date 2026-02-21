package com.app.onestepback.domain.dto.crowdfunding;

import com.app.onestepback.domain.type.crowdfunding.CrowdFundingStatus;
import com.app.onestepback.global.util.FileUrlSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrowdFundingDTO {
    public record PublicList(
            Long id,
            String title,
            Long artistId,
            String artistName,
            @JsonSerialize(using = FileUrlSerializer.class)
            String thumbnailPath,
            Integer achieveRate,
            Long collectedAmount,
            CrowdFundingStatus status,
            Long daysLeft
    ) {
    }

    public record ListView(
            Long id,
            String title,
            CrowdFundingStatus status,
            Long targetAmount,
            Long currentAmount,
            Integer achieveRate,   // 달성률 (%)
            LocalDateTime startDate,
            LocalDateTime endDate,
            @JsonSerialize(using = FileUrlSerializer.class)
            String mainImgPath,    // 썸네일 경로
            Long daysLeft          // 남은 기간 (D-Day)
    ) {
    }

    public record Detail(
            Long id,
            Long artistId,
            Long writerId,
            String writerNickname, // 작성자 닉네임
            @JsonSerialize(using = FileUrlSerializer.class)
            String writerProfilePath,

            String rejectReason,
            String title,
            String content,
            CrowdFundingStatus status,

            Long targetAmount,
            Long currentAmount,
            Integer achieveRate,   // 달성률
            Long participantCount, // 참여자 수

            LocalDateTime startDate,
            LocalDateTime endDate,
            Long daysLeft,         // D-Day

            @JsonSerialize(using = FileUrlSerializer.class)
            String mainImgPath
    ) {
    }

    public record PaymentView(
            Long fundingId,
            String title,           // 프로젝트 제목
            String artistName,      // 아티스트 이름 (블로그명 or 닉네임)
            LocalDateTime endDate,  // 펀딩 종료일 (정보 제공용)

            // 후원자 정보 (결제 모듈에 태울 기본 정보)
            Long memberId,
            String memberName,
            String memberEmail,

            @JsonSerialize(using = FileUrlSerializer.class)
            String mainImgPath      // 썸네일 (배경이나 확인용)
    ) {
    }

    public record Receipt(
            Long fundingId,
            Long artistId,
            String projectTitle,
            String artistName,
            Long amount,
            LocalDateTime paymentDate
    ) {}
}