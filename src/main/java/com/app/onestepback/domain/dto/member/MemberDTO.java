package com.app.onestepback.domain.dto.member;

import com.app.onestepback.global.util.FileUrlSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MemberDTO {

    public record Info(
            Long id,
            String email,
            String nickname,
            @JsonSerialize(using = FileUrlSerializer.class)
            String profilePath,
            String description
    ) {
    }
}