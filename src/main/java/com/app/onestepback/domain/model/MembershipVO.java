package com.app.onestepback.domain.model;

import lombok.*;

@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MembershipVO {
    private Long id;
    private Long artistId;
    private String name;
    private String description;
    private String imageId;
    private Long price;
    private Integer levelOrder; // 1(하위) -> 3(상위)
    private Long subscriberCount;
    private boolean active;
    private String createdTime;
    private String updatedTime;
}