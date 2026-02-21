package com.app.onestepback.web.api.v1.file;

import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.global.annotation.LoginUser;
import com.app.onestepback.global.exception.BusinessException;
import com.app.onestepback.global.exception.ErrorCode;
import com.app.onestepback.global.util.FileTokenProvider;
import com.app.onestepback.infra.file.FileResource;
import com.app.onestepback.service.file.FileInfo;
import com.app.onestepback.service.file.FileService;
import com.app.onestepback.service.file.cmd.SaveFileCmd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileApiV1 {

    private final FileService fileService;
    private final FileTokenProvider tokenProvider;

    /**
     * 고유 식별자와 보안 토큰을 기반으로 물리적 파일 스트림을 반환합니다.
     * <p>
     * 인가되지 않은 접근(토큰 누락, 위조, 만료)을 차단하며,
     * 응답에 브라우저 캐싱 정책을 적용하여 네트워크 I/O 부하를 최적화합니다.
     * </p>
     *
     * @param fileId 조회할 파일의 고유 식별자
     * @param token  파일 접근 권한을 증명하는 단기 유효 보안 토큰
     * @return 파일 바이너리 스트림 및 미디어 타입이 포함된 리소스 응답
     */
    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> display(
            @PathVariable("fileId") String fileId,
            @RequestParam(value = "token", required = false) String token
    ) {
        if (!tokenProvider.validateToken(fileId, token)) {
            log.warn("[File API] 비정상적인 파일 접근이 차단되었습니다. ID: {}, Token: {}", fileId, token);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        FileResource fileResource = fileService.loadFile(fileId);

        try {
            String contentType = fileResource.getContentType();
            if (contentType == null || contentType.isBlank()) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePrivate().immutable())
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(fileResource.getContentLength())
                    .body(new InputStreamResource(fileResource.inputStream()));

        } catch (Exception e) {
            fileResource.close();
            throw e;
        }
    }

    /**
     * 클라이언트로부터 전달받은 단일 파일을 시스템 임시(TEMP) 영역에 저장하고 메타데이터를 반환합니다.
     *
     * @param user 현재 인가된 사용자의 세션 객체
     * @param file 업로드할 멀티파트(Multipart) 파일 데이터
     * @return 스토리지에 저장된 파일의 식별자 및 메타데이터 정보
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @LoginUser SessionUser user,
            @RequestPart("file") MultipartFile file
    ) {
        FileInfo fileInfo = fileService.store(
                new SaveFileCmd(
                        user.getId(),
                        null,
                        "TEMP",
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getSize(),
                        () -> {
                            try {
                                return file.getInputStream();
                            } catch (IOException e) {
                                log.error("[File API] 파일 스트림 추출 중 오류가 발생하였습니다. FileName: {}", file.getOriginalFilename(), e);
                                throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR);
                            }
                        }
                )
        );

        return ResponseEntity.ok(fileInfo);
    }
}