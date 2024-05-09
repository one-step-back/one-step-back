package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.artist.*;
import com.app.onestepback.domain.dto.postElements.PostFileDTO;
import com.app.onestepback.domain.vo.Pagination;
import com.app.onestepback.domain.vo.PostFileVO;
import com.app.onestepback.domain.vo.PostTagVO;
import com.app.onestepback.repository.*;
import com.app.onestepback.service.file.PostFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArtistPostServiceImpl implements ArtistPostService {
    private final ArtistDAO artistDAO;
    private final ArtistPostDAO artistPostDAO;
    private final PostTagDAO postTagDAO;
    private final PostFileDAO postFileDAO;
    private final PostDAO postDAO;

    private final PostFileService postFileService;

    @Override
    public Optional<ArtistDTO> getArtist(Long memberId) {
        return artistDAO.getArtist(memberId);
    }

    @Override
    public int getPostCount(Long memberId) {
        return artistPostDAO.getCountOfPost(memberId);
    }

    @Override
    public List<ArtistPostListDTO> getArtistPostsPage(Long memberId, Long viewerId, Pagination pagination) {
        return artistPostDAO.getArtistPostsPage(memberId, viewerId, pagination);
    }

    @Override
    public List<String> getAllTags(Long postId) {
        return postTagDAO.getAllTags(postId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePost(ArtistPostRegisterDTO artistPostRegisterDTO) throws IOException {
        // 포스트 저장
        artistPostDAO.savePost(artistPostRegisterDTO);
        artistPostDAO.saveArtistPost(artistPostRegisterDTO.getPostId());

        // 포스트 태그 저장
        List<PostTagVO> postTags = artistPostRegisterDTO.getTags().stream()
                .map(tagName -> PostTagVO.builder()
                        .postId(artistPostRegisterDTO.getPostId())
                        .postTagName(tagName)
                        .build())
                .collect(Collectors.toList());
        postTagDAO.saveAllTags(postTags);

        // 파일 저장 (파일 등록을 했을경우에만 실행)
        if (artistPostRegisterDTO.getFiles().stream().anyMatch(file -> file.getSize() > 0)) {
            postFileService.registerFiles(artistPostRegisterDTO.getFiles(), artistPostRegisterDTO.getPostId());
        }
    }

    @Override
    public ArtistPostDetailDTO getPostDetail(Long artistId, Long postId) {
        // tag들은 leftJoin의 결과물로 가져왔지만 postFile까지 leftJoin하게 될 경우 카르테시안곱에 의해
        // 결과가 배로 늘어날 수 있다. 이는 성능 결과에 매우 치명적일 수 있음.
        // todo : 두개의 독립 쿼리를 날리는 것과 결과가 많이 나타나는 카르테시안곱 중 어떤 것이 더욱 효과적일지 테스트 할 필요성이 있음.
        ArtistPostDetailDTO content = artistPostDAO.getPost(artistId, postId).orElseThrow(
                () -> new NoSuchElementException("게시글을 찾을 수 없음")
        );

        List<PostFileDTO> imgFiles = postFileDAO.getAllFiles(content.getPostId());
        content.setImgFiles(imgFiles);

        return content;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editPost(ArtistPostDTO artistPostDTO, int numberOfTags, List<String> uuids, List<MultipartFile> uploadFiles) {
        artistPostDAO.editPost(artistPostDTO);

//        postTagDAO.deletePostTag(artistPostDTO.getId());
//        for (int i = 1; i <= numberOfTags; i++) {
//            try {
//                PostTagVO postTagVO = new PostTagVO();
//
//                String tagName = "tag" + i;
//                Field field = artistPostDTO.getClass().getDeclaredField(tagName);
//                field.setAccessible(true);
//
//                postTagVO.setPostId(artistPostDTO.getId());
//                postTagVO.setPostTagName((String) field.get(artistPostDTO));
//
//                postTagDAO.savePostTag(postTagVO);
//            } catch (NoSuchFieldException | IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
//        if (uuids != null) {
//            for (int i = 0; i < uploadFiles.size(); i++) {
//                PostFileVO postFileVO = new PostFileVO();
//
//                postFileVO.setPostId(artistPostDTO.getId());
//                postFileVO.setFileName(uuids.get(i) + "_" + uploadFiles.get(i).getOriginalFilename());
//                postFileVO.setFilePath(getPath());
//
//                postFileDAO.saveFile(postFileVO);
//            }
//        }
    }

    @Override
    public void erasePost(Long id) {
        postDAO.erasePost(id);
    }


    private String getPath() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }
}
