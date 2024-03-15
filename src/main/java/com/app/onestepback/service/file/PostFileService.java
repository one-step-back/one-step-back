package com.app.onestepback.service.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostFileService {
    public void eraseFile(Long id);

    void registerFiles(List<MultipartFile> files, Long postId) throws IOException;
}
