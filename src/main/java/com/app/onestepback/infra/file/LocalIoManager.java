package com.app.onestepback.infra.file;

import com.app.onestepback.infra.file.cmd.PutCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.Optional;

/**
 * 로컬 파일 시스템을 기반으로 파일 입출력(I/O) 작업을 수행하는 매니저 구현체입니다.
 * <p>
 * 애플리케이션이 구동되는 서버의 물리적 디스크 공간을 활용하여 파일을 저장, 조회, 삭제하며,
 * 악의적인 경로 조작(Directory Traversal) 공격을 방어하기 위한 보안 검증 로직을 포함합니다.
 * </p>
 */
@Slf4j
@Component
public class LocalIoManager implements IoManager {

    private final Path rootPath;

    /**
     * 지정된 업로드 디렉토리를 기준 경로로 설정하고, 존재하지 않을 경우 디렉토리를 생성합니다.
     *
     * @param uploadDir application.yml 등에 정의된 로컬 업로드 최상위 경로
     */
    public LocalIoManager(@Value("${file.upload-dir}") String uploadDir) {
        this.rootPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.rootPath);
        } catch (IOException e) {
            throw new RuntimeException("업로드 기준 디렉토리를 생성할 수 없습니다.", e);
        }
    }

    @Override
    public FileObjectMetadata put(PutCommand cmd) throws IOException {
        Path targetPath = resolvePath(cmd.key());

        if (targetPath.getParent() != null) {
            Files.createDirectories(targetPath.getParent());
        }

        Files.copy(cmd.in(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        long size = Files.size(targetPath);
        Instant lastModified = Files.getLastModifiedTime(targetPath).toInstant();

        return FileObjectMetadata.of(
                cmd.key(),
                cmd.contentType(),
                size,
                lastModified
        );
    }

    @Override
    public boolean delete(String key) throws IOException {
        Path targetPath = resolvePath(key);
        return Files.deleteIfExists(targetPath);
    }

    @Override
    public FileResource openStream(String key) throws IOException {
        Path targetPath = resolvePath(key);

        if (!Files.exists(targetPath)) {
            throw new FileNotFoundException("요청하신 파일을 찾을 수 없습니다: " + key);
        }

        long size = Files.size(targetPath);
        Instant lastModified = Files.getLastModifiedTime(targetPath).toInstant();

        String contentType = Files.probeContentType(targetPath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        FileObjectMetadata metadata = FileObjectMetadata.of(key, contentType, size, lastModified);

        return new FileResource(metadata, Files.newInputStream(targetPath));
    }

    @Override
    public Optional<FileObjectMetadata> head(String key) throws IOException {
        Path targetPath = resolvePath(key);

        if (!Files.exists(targetPath)) {
            return Optional.empty();
        }

        long size = Files.size(targetPath);
        Instant lastModified = Files.getLastModifiedTime(targetPath).toInstant();

        String contentType = Files.probeContentType(targetPath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return Optional.of(FileObjectMetadata.of(key, contentType, size, lastModified));
    }

    /**
     * 클라이언트로부터 전달받은 파일 식별자(Key)를 안전한 물리적 경로로 변환합니다.
     * <p>
     * 상위 디렉토리 이동("../")과 같은 경로 조작 시도를 차단하여
     * 설정된 루트 디렉토리 외부로의 접근을 원천적으로 제한합니다.
     * </p>
     *
     * @param key 클라이언트가 요청한 파일 식별자
     * @return 검증 및 정규화가 완료된 로컬 파일 경로
     * @throws SecurityException 허용되지 않은 경로 접근 시도 시 발생
     */
    private Path resolvePath(String key) {
        Path resolvedPath = this.rootPath.resolve(key).normalize();

        if (!resolvedPath.startsWith(this.rootPath)) {
            throw new SecurityException("허용되지 않은 파일 접근 경로입니다: " + key);
        }

        return resolvedPath;
    }
}