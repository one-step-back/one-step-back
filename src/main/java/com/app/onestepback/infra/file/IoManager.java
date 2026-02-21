package com.app.onestepback.infra.file;

import com.app.onestepback.infra.file.cmd.PutCommand;

import java.io.IOException;
import java.util.Optional;

/**
 * 물리적 또는 클라우드 스토리지(File System, S3 등)와의 직접적인 입출력(I/O) 작업을
 * 추상화하여 제공하는 매니저 인터페이스입니다.
 */
public interface IoManager {

    /**
     * 전달받은 커맨드 객체의 정보를 바탕으로 스토리지에 파일을 저장합니다.
     *
     * @param cmd 파일 데이터와 메타정보가 포함된 저장 명령 객체
     * @return 저장 성공 후 생성된 파일의 메타데이터
     * @throws IOException 스토리지 입출력 처리 중 오류 발생 시
     */
    FileObjectMetadata put(PutCommand cmd) throws IOException;

    /**
     * 지정된 식별자에 해당하는 파일을 스토리지에서 물리적으로 삭제합니다.
     *
     * @param key 삭제할 파일의 고유 식별 경로
     * @return 삭제 성공 시 true, 파일이 존재하지 않거나 삭제 실패 시 false
     * @throws IOException 스토리지 접근 및 삭제 처리 중 오류 발생 시
     */
    boolean delete(String key) throws IOException;

    /**
     * 지정된 식별자의 파일을 읽어들여 메타데이터와 입력 스트림을 포함한 리소스 객체를 반환합니다.
     *
     * @param key 조회할 파일의 고유 식별 경로
     * @return 파일 리소스 객체 (사용 후 반드시 close 처리 필요)
     * @throws IOException 파일이 존재하지 않거나 스트림 개방 중 오류 발생 시
     */
    FileResource openStream(String key) throws IOException;

    /**
     * 파일의 본문 데이터(바이너리)를 제외한 메타데이터 정보만을 조회합니다.
     *
     * @param key 조회할 파일의 고유 식별 경로
     * @return 파일이 존재할 경우 메타데이터를 포함한 Optional, 존재하지 않을 경우 empty
     * @throws IOException 스토리지 접근 중 오류 발생 시
     */
    Optional<FileObjectMetadata> head(String key) throws IOException;
}