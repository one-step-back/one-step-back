package com.app.onestepback.service.file;

import com.app.onestepback.domain.model.FileVO;
import com.app.onestepback.global.exception.BusinessException;
import com.app.onestepback.global.exception.ErrorCode;
import com.app.onestepback.global.util.ULIDs;
import com.app.onestepback.infra.file.FileResource;
import com.app.onestepback.infra.file.IoManager;
import com.app.onestepback.infra.file.cmd.PutCommand;
import com.app.onestepback.repository.FileMapper;
import com.app.onestepback.service.file.cmd.SaveFileCmd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * 시스템 설정 스토리지(로컬/S3 등)와의 I/O 제어 및 데이터베이스 메타데이터 동기화를 수행하는 파일 서비스 구현체입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final IoManager ioManager;
    private final FileMapper fileMapper;

    @Override
    @Transactional
    public FileInfo store(SaveFileCmd cmd) {
        String ulid = ULIDs.newMonotonicUlid();
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        String originalFilename = cmd.originalFilename() != null ? cmd.originalFilename() : "";
        String extension = StringUtils.getFilenameExtension(originalFilename);
        String saveFileName = ulid + "." + extension;

        String key = datePath + "/" + saveFileName;

        try (InputStream inputStream = cmd.content().get()) {
            PutCommand putCmd = new PutCommand(key, inputStream, cmd.contentLength(), cmd.contentType());
            ioManager.put(putCmd);
        } catch (IOException e) {
            log.error("[File Storage] 스토리지 물리 파일 저장 중 I/O 오류가 발생하였습니다. File: {}", originalFilename, e);
            throw new BusinessException(ErrorCode.FILE_SYSTEM_ERROR);
        }

        FileVO fileVO = FileVO.builder()
                .id(ulid)
                .fileOwnerId(cmd.ownerId())
                .fileOriginalName(originalFilename)
                .fileName(saveFileName)
                .filePath(datePath)
                .fileSize(cmd.contentLength())
                .fileExtension(getExtension(originalFilename))
                .targetId(cmd.targetId())
                .targetGb(cmd.targetGb())
                .build();
        fileMapper.insert(fileVO);

        return toFileInfo(fileVO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FileInfo> getFile(String fileId) {
        return fileMapper.selectById(fileId).map(this::toFileInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public FileResource loadFile(String fileId) {
        FileVO fileVO = fileMapper.selectById(fileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));

        String key = fileVO.getFilePath() + "/" + fileVO.getFileName();

        try {
            return ioManager.openStream(key);
        } catch (IOException e) {
            log.error("[File Storage] 물리적 파일 스트림 개방 중 오류가 발생하였습니다. Key: {}", key, e);
            throw new BusinessException(ErrorCode.FILE_SYSTEM_ERROR);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FileInfo> getFileHeadMeta(String fileId) {
        return getFile(fileId);
    }

    @Override
    @Transactional
    public void connectFiles(Long targetId, String targetGb, List<String> fileIds, Long ownerId) {
        if (fileIds == null || fileIds.isEmpty()) return;
        fileMapper.updateFileTarget(targetId, targetGb, fileIds, ownerId);
    }

    @Override
    @Transactional
    public void disconnectFiles(List<String> fileIds, Long ownerId) {
        if (fileIds == null || fileIds.isEmpty()) return;
        fileMapper.updateFilesToDeletable(fileIds, ownerId);
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }

    private FileInfo toFileInfo(FileVO vo) {
        return new FileInfo(
                vo.getId(),
                vo.getFileOriginalName(),
                vo.getFileName(),
                vo.getFilePath(),
                vo.getFileExtension(),
                vo.getFileSize()
        );
    }
}