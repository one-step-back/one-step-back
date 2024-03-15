package com.app.onestepback.api.controller;

import com.app.onestepback.service.file.PostFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/file/*")
@RequiredArgsConstructor
public class FileController {
    private final PostFileService postFileService;

    @Value("${root.dir}")
    private String ROOT_PATH;

    //    파일 업로드
    @PostMapping("upload")
    public List<String> upload(@RequestParam("uploadFile") List<MultipartFile> uploadFiles) throws IOException {
        List<String> uuids = new ArrayList<>();
        File file = new File(ROOT_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }

        for (int i = 0; i < uploadFiles.size(); i++) {
            uuids.add(UUID.randomUUID().toString());
            uploadFiles.get(i).transferTo(new File(ROOT_PATH, uuids.get(i) + "_" + uploadFiles.get(i).getOriginalFilename()));
            if (Objects.requireNonNull(uploadFiles.get(i).getContentType()).startsWith("image")) {
                FileOutputStream out = new FileOutputStream(new File(ROOT_PATH, "t_" + uuids.get(i) + "_" + uploadFiles.get(i).getOriginalFilename()));
                Thumbnailator.createThumbnail(uploadFiles.get(i).getInputStream(), out, 200, 200);
                out.close();
            }
        }

        return uuids;
    }

    @GetMapping("display")
    public byte[] display(String fileName) throws IOException {
        return FileCopyUtils.copyToByteArray(new File(ROOT_PATH, fileName));
    }

    @PostMapping("delete")
    public Long eraseFile(@RequestParam("fileId") Long fileId) {
        log.info(fileId.toString());

        postFileService.eraseFile(fileId);

        return fileId;
    }

    private String getPath() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }
}
