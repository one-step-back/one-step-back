package com.app.onestepback.domain.dto.membership;

import com.app.onestepback.global.util.FileUrlSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MembershipDetailDTO {
    private Long id;
    private String name;        // 멤버십 이름
    private String description; // 설명
    private Long price;         // 가격

    @JsonSerialize(using = FileUrlSerializer.class)
    private String imagePath;
    private boolean active;

    private Long artistId;
    private String artistNickname;
    @JsonSerialize(using = FileUrlSerializer.class)
    private String artistProfilePath;
}