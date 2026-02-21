package com.app.onestepback.service.file.cmd;

import org.springframework.web.multipart.MultipartFile;

/**
 * 클라이언트로부터 전달받은 멀티파트(Multipart) 파일 업로드 요청 데이터를 캡슐화한 커맨드 객체입니다.
 */
public record UploadCmd(
        MultipartFile file,
        long ownerId,
        Long targetId,
        String targetGb
) {
}