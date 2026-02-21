package com.app.onestepback.service.file;

import com.app.onestepback.global.exception.BusinessException;
import com.app.onestepback.global.exception.ErrorCode;
import com.app.onestepback.service.file.cmd.SaveFileCmd;
import com.app.onestepback.service.file.cmd.UploadCmd;
import com.app.onestepback.service.file.variant.VariantJob;
import com.app.onestepback.service.file.variant.VariantProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * 이미지 미디어 파일의 업로드 처리 및 모자이크 등의 비동기 변환 이벤트를 발행하는 서비스 구현체입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements MediaUploadUseCase {

    private final FileService fileService;
    private final VariantProducer variantProducer;

    @Override
    @Transactional
    public FileInfo upload(UploadCmd cmd) {
        FileInfo savedFile = fileService.store(new SaveFileCmd(
                cmd.ownerId(),
                cmd.targetId(),
                cmd.targetGb(),
                cmd.file().getOriginalFilename(),
                cmd.file().getContentType(),
                cmd.file().getSize(),
                () -> {
                    try {
                        return cmd.file().getInputStream();
                    } catch (IOException e) {
                        log.error("[Media Upload] 클라이언트 파일 스트림 추출 중 오류가 발생하였습니다.", e);
                        throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR);
                    }
                }
        ));

        process(savedFile.id());

        return savedFile;
    }

    @Override
    public void process(String fileId) {
        fileService.getFile(fileId).ifPresent(file -> {
            if (!isImage(file.fileExtension())) {
                return;
            }

            /* 멱등성 검증: 모자이크 변환 파일이 이미 존재하는 경우 메시지 발행을 생략합니다. */
            String mosaicId = "mq_" + file.id();
            if (fileService.getFile(mosaicId).isPresent()) {
                return;
            }

            log.info("[Media Processor] 비동기 이미지 모자이크 변환 작업을 메시지 큐에 발행합니다. Target File ID: {}", file.id());
            variantProducer.publish(VariantJob.builder()
                    .fileId(file.id())
                    .originalPath(file.filePath())
                    .savedFileName(file.fileName())
                    .type(VariantJob.VariantType.MOSAIC)
                    .build());
        });
    }

    private boolean isImage(String extension) {
        if (extension == null) return false;
        return switch (extension.toLowerCase()) {
            case "jpg", "jpeg", "png", "webp", "bmp" -> true;
            default -> false;
        };
    }
}