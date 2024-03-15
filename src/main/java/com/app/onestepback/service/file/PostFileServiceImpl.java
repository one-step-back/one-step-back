package com.app.onestepback.service.file;

import com.app.onestepback.domain.vo.PostFileVO;
import com.app.onestepback.repository.PostFileDAO;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostFileServiceImpl implements PostFileService {
    private final PostFileDAO postFileDAO;

    @Value("${root.dir}")
    private String ROOT_PATH;

    @Override
    public void eraseFile(Long id) {
        postFileDAO.eraseFile(id);
    }

    @Override
    public void registerFiles(List<MultipartFile> files, Long postId) throws IOException {
        List<PostFileVO> postFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            PostFileVO postFileVO = uploadFile(file);
            postFileVO.setPostId(postId);
            postFiles.add(postFileVO);
        }

        postFileDAO.saveAllFiles(postFiles);
    }

    // 파일 저장은 filePath와 uuid + originalFileName으로 DB에 저장이 된다.
    private PostFileVO uploadFile(MultipartFile file) throws IOException {
        String date = getFilePath();
        // 파일 저장 경로는 오늘의 날짜
        File filePath = new File(ROOT_PATH, date);
        // UUID 생성
        UUID uuid = UUID.randomUUID();
        // 파일의 본래이름 (공백문자는 제거한다.)
        String originalName = Objects.requireNonNull(file.getOriginalFilename()).replace("\\s", "");

        // 없을 경우 파일 생성
        if (!filePath.exists()) {
            filePath.mkdirs();
        }

        // uuid와 파일이름을 합친 원본파일을 특정된 경로에 추가시킨다.
        String originalFile = uuid + "_" + originalName;
        File uploadOriginalFile = new File(filePath, originalFile);
        file.transferTo(uploadOriginalFile);

        // 추가된 파일이 사진일 경우 썸네일레이터로 generate된 사진도 같이 저장한다.
        if (Files.probeContentType(uploadOriginalFile.toPath()).startsWith("image")) {
            File thumbnailFile = new File(filePath, "t_" + originalFile);

            try (FileOutputStream out = new FileOutputStream(thumbnailFile);
                 InputStream in = file.getInputStream()) {
                Thumbnailator.createThumbnail(in, out, 300, 225);
                // try를 사용하면 out.close()는 생략할 수 있다
            }
        }

        return PostFileVO.builder()
                .filePath(date)
                .fileName(originalName)
                .build();
    }

    private String getFilePath() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }
}
