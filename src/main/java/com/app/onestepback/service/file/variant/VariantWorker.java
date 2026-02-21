package com.app.onestepback.service.file.variant;

import com.app.onestepback.domain.model.FileVO;
import com.app.onestepback.infra.file.FileResource;
import com.app.onestepback.infra.file.IoManager;
import com.app.onestepback.infra.file.cmd.PutCommand;
import com.app.onestepback.repository.FileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 비동기적으로 발행된 미디어 변환 작업(Variant Job)을 실제로 수행하는 워커 컴포넌트입니다.
 * <p>
 * 외부 시스템(ImageMagick 등)을 호출하여 이미지 처리를 수행하며,
 * 변환된 파일을 스토리지에 재저장하고 데이터베이스 메타데이터를 갱신합니다.
 * 무거운 외부 프로세스 대기 시간 동안 데이터베이스 커넥션 풀을 고갈시키지 않기 위해
 * 명시적인 클래스/메서드 레벨의 @Transactional 선언을 의도적으로 배제하였습니다.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VariantWorker {

    private static final String MAGICK_PATH = "magick";
    private final IoManager ioManager;
    private final FileMapper fileMapper;

    /**
     * 전달받은 작업 유형에 따라 적절한 미디어 변환 로직을 분기하여 실행합니다.
     *
     * @param job 수행할 변환 작업 객체
     * @throws Exception 파일 입출력 또는 프로세스 실행 중 오류 발생 시
     */
    public void handle(VariantJob job) throws Exception {
        if (job.type() == VariantJob.VariantType.MOSAIC) {
            processMosaic(job);
        }
    }

    /**
     * 원본 이미지를 다운로드하여 모자이크(가우시안 블러) 처리를 수행한 뒤
     * 새로운 파생 파일로 스토리지 및 데이터베이스에 저장합니다.
     *
     * @param job 모자이크 처리 대상 작업 객체
     * @throws Exception 이미지 매직 프로세스 실패 또는 I/O 예외 발생 시
     */
    private void processMosaic(VariantJob job) throws Exception {
        log.info("[Variant Processing] 모자이크 변환 작업을 시작합니다. Target: {}", job.savedFileName());

        File tempOriginal = File.createTempFile("original_", "_" + UUID.randomUUID());
        File tempOutput = File.createTempFile("mosaic_", "_" + UUID.randomUUID());

        try {
            String storageKey = job.originalPath() + "/" + job.savedFileName();

            try (FileResource resource = ioManager.openStream(storageKey);
                 InputStream is = resource.inputStream()) {
                Files.copy(is, tempOriginal.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            ProcessBuilder pb = new ProcessBuilder(
                    MAGICK_PATH,
                    "convert",
                    tempOriginal.getAbsolutePath(),
                    "-resize", "2%",
                    "-gaussian-blur", "0x5",
                    "-resize", "5000%",
                    tempOutput.getAbsolutePath()
            );

            pb.redirectErrorStream(true);
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                String errorMsg = new String(process.getInputStream().readAllBytes());
                throw new RuntimeException("ImageMagick 처리 중 치명적 오류가 발생하였습니다: " + errorMsg);
            }

            String variantName = "mq_" + job.savedFileName();
            String variantKey = job.originalPath() + "/" + variantName;

            try (FileInputStream fis = new FileInputStream(tempOutput)) {
                ioManager.put(new PutCommand(
                        variantKey,
                        fis,
                        tempOutput.length(),
                        "image/jpeg"
                ));
            }

            FileVO originalVo = fileMapper.selectById(job.fileId())
                    .orElseThrow(() -> new IllegalArgumentException("원본 파일의 메타데이터를 찾을 수 없습니다: " + job.fileId()));

            FileVO variantVo = FileVO.builder()
                    .id("mq_" + originalVo.getId())
                    .fileOwnerId(originalVo.getFileOwnerId())
                    .fileOriginalName("mosaic_" + originalVo.getFileOriginalName())
                    .fileName(variantName)
                    .filePath(job.originalPath())
                    .fileSize(tempOutput.length())
                    .fileExtension(originalVo.getFileExtension())
                    .targetId(originalVo.getTargetId())
                    .targetGb(originalVo.getTargetGb() + "_MOSAIC")
                    .build();

            fileMapper.insert(variantVo);
            log.info("[Variant Processing] 모자이크 변환 및 스토리지 저장이 성공적으로 완료되었습니다. Key: {}", variantKey);

        } finally {
            if (tempOriginal.exists() && !tempOriginal.delete()) {
                log.warn("[Variant Processing] 시스템 임시 파일 삭제에 실패하였습니다: {}", tempOriginal.getAbsolutePath());
            }
            if (tempOutput.exists() && !tempOutput.delete()) {
                log.warn("[Variant Processing] 시스템 임시 파일 삭제에 실패하였습니다: {}", tempOutput.getAbsolutePath());
            }
        }
    }
}