package com.app.onestepback.service.file;

import com.app.onestepback.infra.file.FileResource;
import com.app.onestepback.service.file.cmd.SaveFileCmd;

import java.util.List;
import java.util.Optional;

/**
 * 파일의 물리적 입출력(I/O) 및 영속성(DB) 생명주기를 총괄하는 핵심 서비스 인터페이스입니다.
 */
public interface FileService {

    /**
     * 물리적 스토리지에 파일을 저장하고 데이터베이스에 메타데이터를 기록합니다.
     *
     * @param cmd 파일 저장 요청 데이터가 담긴 커맨드 객체
     * @return 저장된 파일의 최종 메타데이터 (ID 발급 포함)
     */
    FileInfo store(SaveFileCmd cmd);

    /**
     * 고유 식별자를 기반으로 파일의 메타데이터를 조회합니다.
     *
     * @param fileId 파일 고유 식별자
     * @return 파일 메타데이터가 포함된 Optional 객체
     */
    Optional<FileInfo> getFile(String fileId);

    /**
     * 고유 식별자를 기반으로 스토리지에서 파일의 실제 바이너리 스트림을 열어 반환합니다.
     *
     * @param fileId 파일 고유 식별자
     * @return 파일 바이너리 스트림과 메타데이터가 결합된 리소스 객체
     */
    FileResource loadFile(String fileId);

    /**
     * 스토리지와 데이터베이스 간의 상태 동기화를 검증하기 위해 메타데이터 헤더 정보를 조회합니다.
     *
     * @param fileId 파일 고유 식별자
     * @return 검증용 파일 메타데이터 Optional 객체
     */
    Optional<FileInfo> getFileHeadMeta(String fileId);

    /**
     * 다수의 파일을 특정 도메인 타겟(게시글, 프로필 등)에 연결하여 소유권을 할당합니다.
     *
     * @param targetId    연결할 대상 도메인의 고유 식별자
     * @param targetGb    도메인 구분 코드 (예: FEED, ARTIST)
     * @param fileIds     연결할 파일 식별자 목록
     * @param fileOwnerId 파일의 소유자(회원) 고유 식별자
     */
    void connectFiles(Long targetId, String targetGb, List<String> fileIds, Long fileOwnerId);

    /**
     * 특정 도메인 타겟과의 연결을 해제하고 해당 파일을 삭제 가능 상태로 변경합니다.
     *
     * @param fileIds 연결 해제할 파일 식별자 목록
     * @param ownerId 소유권 검증을 위한 회원의 고유 식별자
     */
    void disconnectFiles(List<String> fileIds, Long ownerId);
}