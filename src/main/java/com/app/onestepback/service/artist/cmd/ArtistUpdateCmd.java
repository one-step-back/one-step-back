package com.app.onestepback.service.artist.cmd;

import org.springframework.web.multipart.MultipartFile;

public record ArtistUpdateCmd(
        long memberId,

        String blogName,
        String description,
        MultipartFile blogImage,

        String memberNickname,
        MultipartFile profileImage
) {
}
