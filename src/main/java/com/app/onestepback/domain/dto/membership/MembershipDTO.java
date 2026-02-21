package com.app.onestepback.domain.dto.membership;

import com.app.onestepback.global.util.FileUrlSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MembershipDTO {
    private Long id;
    private Long artistId;
    private String name;
    private String description;
    private Long price;
    private Integer levelOrder;
    private String imageId;
    @JsonSerialize(using = FileUrlSerializer.class)
    private String imagePath;

    private Long subscriberCount;
    private boolean active;

    private boolean subscribing;
}