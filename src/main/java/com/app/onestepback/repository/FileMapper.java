package com.app.onestepback.repository;

import com.app.onestepback.domain.model.FileVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface FileMapper {
    // 파일 정보 저장
    void insert(FileVO fileVO);

    // 파일 단건 조회
    Optional<FileVO> selectById(String id);

    void updateFileTarget(
            @Param("targetId") Long targetId,
            @Param("targetGb") String targetGb,
            @Param("fileIds") List<String> fileIds,
            @Param("fileOwnerId") Long fileOwnerId
    );

    void updateFilesToDeletable(List<String> fileIds, Long fileOwnerId);
}
