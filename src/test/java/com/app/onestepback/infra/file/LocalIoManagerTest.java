package com.app.onestepback.infra.file;

import com.app.onestepback.infra.file.cmd.PutCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class LocalIoManagerTest {
    private LocalIoManager localIoManager;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // 임시 폴더 경로를 주입하여 매니저 생성
        localIoManager = new LocalIoManager(tempDir.toString());
    }

    @Test
    @DisplayName("파일 저장(put) 테스트 - 파일이 실제 경로에 생성되어야 한다")
    void putTest() throws IOException {
        // given
        String key = "images/profile/test_upload.jpg";
        byte[] fileData = loadTestFile(); // resources/test.jpg 읽어오기

        // InputStream은 한 번 읽으면 사라지므로 테스트용으로 매번 새로 생성
        InputStream inputStream = new ByteArrayInputStream(fileData);

        PutCommand command = new PutCommand(
                key,
                inputStream,
                fileData.length,
                "image/jpeg"
        );

        // when
        FileObjectMetadata metadata = localIoManager.put(command);

        // then
        // 1. 메타데이터 검증
        assertThat(metadata.key()).isEqualTo(key);
        assertThat(metadata.contentLength()).isEqualTo(fileData.length);

        // 2. 실제 파일 존재 여부 검증
        Path savedFile = tempDir.resolve(key);
        assertThat(Files.exists(savedFile)).isTrue();
        assertThat(Files.size(savedFile)).isEqualTo(fileData.length);
    }

    @Test
    @DisplayName("파일 읽기(openStream) 테스트 - 저장된 내용을 그대로 읽어와야 한다")
    void openStreamTest() throws IOException {
        // given
        String key = "download/test.jpg";
        byte[] originalData = loadTestFile();

        // 먼저 파일 저장
        localIoManager.put(new PutCommand(key, new ByteArrayInputStream(originalData), originalData.length, "image/jpeg"));

        // when
        FileResource resource = localIoManager.openStream(key);

        // then
        try (resource) { // try-with-resources로 자동 닫기
            byte[] readData = FileCopyUtils.copyToByteArray(resource.inputStream());

            assertThat(readData).isEqualTo(originalData); // 원본과 읽은 데이터가 같은지 비교
            assertThat(resource.getContentLength()).isEqualTo(originalData.length);
        }
    }

    @Test
    @DisplayName("파일 삭제(delete) 테스트 - 삭제 후에는 파일이 없어야 한다")
    void deleteTest() throws IOException {
        // given
        String key = "delete/target.jpg";
        byte[] data = loadTestFile();
        localIoManager.put(new PutCommand(key, new ByteArrayInputStream(data), data.length, "image/jpeg"));

        // 파일이 생겼는지 먼저 확인
        assertThat(Files.exists(tempDir.resolve(key))).isTrue();

        // when
        boolean result = localIoManager.delete(key);

        // then
        assertThat(result).isTrue(); // 삭제 성공 리턴 확인
        assertThat(Files.exists(tempDir.resolve(key))).isFalse(); // 실제 파일 제거 확인
    }

    // --- Helper Method ---

    /**
     * src/test/resources/test.jpg 파일을 읽어서 바이트 배열로 반환
     */
    private byte[] loadTestFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("test.jpg");
        if (!resource.exists()) {
            throw new RuntimeException("테스트 실패: src/test/resources/test.jpg 파일이 없습니다!");
        }
        return FileCopyUtils.copyToByteArray(resource.getInputStream());
    }
}