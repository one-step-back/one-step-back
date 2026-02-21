package com.app.onestepback.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ArtistVO {
    private Long artistId;
    private String artistBlogName;
    private String artistDescription;
    private String artistBlogImageId;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
}