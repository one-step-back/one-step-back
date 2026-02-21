package com.app.onestepback.domain.dto.artist;

import com.app.onestepback.global.util.FileUrlSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@Data
public class ArtistProfileDTO {
    private Long id;
    private String nickname;
    private String blogName;
    private String description;
    @JsonSerialize(using = FileUrlSerializer.class)
    private String profilePath;
    @JsonSerialize(using = FileUrlSerializer.class)
    private String blogImgPath;
    private boolean hasMembership;
}
