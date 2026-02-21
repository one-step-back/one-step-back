package com.app.onestepback.service.artist.cmd;

import org.springframework.web.multipart.MultipartFile;

public record ArtistRegisterCMD(
        Long memberId,
        String blogName,
        String blogDescription,
        MultipartFile blogImage
) {
}
