package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.artist.post.ArtistPostDetailDTO;
import com.app.onestepback.domain.dto.artist.post.ArtistPostEditDTO;
import com.app.onestepback.domain.dto.artist.post.ArtistPostListDTO;
import com.app.onestepback.domain.dto.artist.post.ArtistPostRegisterDTO;
import com.app.onestepback.domain.dto.postElements.PostFileDTO;
import com.app.onestepback.domain.type.post.PostSortType;
import com.app.onestepback.domain.vo.Pagination;
import com.app.onestepback.domain.vo.PostTagVO;
import com.app.onestepback.repository.*;
import com.app.onestepback.service.file.PostFileService;
import com.app.onestepback.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArtistPostServiceImpl implements ArtistPostService {
    private final ArtistPostDAO artistPostDAO;
    private final PostTagDAO postTagDAO;
    private final PostFileDAO postFileDAO;
    private final PostDAO postDAO;

    private final PostFileService postFileService;
    private final TimeUtil timeUtil;

    @Override
    public int getPostCount(Long memberId) {
        return artistPostDAO.getCountOfPost(memberId);
    }

    @Override
    public List<ArtistPostListDTO> getArtistPosts(PostSortType sortType) {
        List<ArtistPostListDTO> contents = artistPostDAO.getArtistPosts(sortType);

        contents.forEach(content -> {
            content.setTimeGap(timeUtil.getTimeGap(content.getCreatedTime()));
        });

        return contents;
    }

    @Override
    public List<ArtistPostListDTO> getArtistPostsPage(Long artistId, Long viewerId, Pagination pagination, PostSortType sortType) {
        List<ArtistPostListDTO> contents = artistPostDAO.getArtistPostsPage(artistId, viewerId, pagination, sortType);

        contents.forEach(content -> {
            content.setTimeGap(timeUtil.getTimeGap(content.getCreatedTime()));
        });

        return contents;
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
    public ArtistPostDetailDTO getPostDetail(Long artistId, Long postId, Long viewerId) {
        // tag들은 leftJoin의 결과물로 가져왔지만 postFile까지 leftJoin하게 될 경우 카르테시안곱에 의해
        // 결과가 배로 늘어날 수 있다. 이는 성능 결과에 매우 치명적일 수 있음.
        // todo : 두개의 독립 쿼리를 날리는 것과 결과가 많이 나타나는 카르테시안곱 중 어떤 것이 더욱 효과적일지 테스트 할 필요성이 있음.
        ArtistPostDetailDTO content = artistPostDAO.getPost(artistId, postId, viewerId).orElseThrow(
                () -> new NoSuchElementException("게시글을 찾을 수 없음")
        );

        List<PostFileDTO> imgFiles = postFileDAO.getAllFiles(content.getPostId());
        content.setImgFiles(imgFiles);

        return content;
    }

    @Override
    public ArtistPostEditDTO getEditPost(Long artistId, Long postId) {
        ArtistPostEditDTO content = artistPostDAO.getEditPost(artistId, postId).orElseThrow(
                () -> new NoSuchElementException("해당 게시글을 조회할 수 없음.")
        );

        List<PostFileDTO> imgFiles = postFileDAO.getAllFiles(content.getPostId());
        content.setFiles(imgFiles);

        return content;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editPost(ArtistPostEditDTO artistPostEditDTO) throws IOException {
        // post 수정
        artistPostDAO.editPost(artistPostEditDTO);

        List<String> savedTags = postTagDAO.getAllTags(artistPostEditDTO.getPostId());
        List<String> tags = artistPostEditDTO.getTags();

        // 새로운 태그 insert
        List<String> newTags = new ArrayList<>(tags);
        newTags.removeAll(savedTags);

        List<PostTagVO> saveTagsList = new ArrayList<>();

        for (String newTag : newTags) {
            PostTagVO postTagVO = PostTagVO.builder()
                    .postId(artistPostEditDTO.getPostId())
                    .postTagName(newTag)
                    .build();
            saveTagsList.add(postTagVO);
        }
        if (!saveTagsList.isEmpty()) {
            postTagDAO.saveAllTags(saveTagsList);
        }

        // 삭제되어야 하는 태그 delete
        List<String> deletedTags = new ArrayList<>(savedTags);
        deletedTags.removeAll(tags);

        List<PostTagVO> deleteTagList = new ArrayList<>();

        for (String deleteTag : deletedTags) {
            PostTagVO postTagVO = PostTagVO.builder()
                    .postId(artistPostEditDTO.getPostId())
                    .postTagName(deleteTag)
                    .build();
            deleteTagList.add(postTagVO);
        }
        if (!deleteTagList.isEmpty()) {
            postTagDAO.deleteAllTags(deleteTagList);
        }

        // 파일 삭제
        if (artistPostEditDTO.getDeletedFiles() != null) {
            postFileDAO.deleteAllFile(artistPostEditDTO.getDeletedFiles());
        }

        // 파일 추가
        if (artistPostEditDTO.getNewFiles().stream().anyMatch(file -> file.getSize() > 0)) {
            postFileService.registerFiles(artistPostEditDTO.getNewFiles(), artistPostEditDTO.getPostId());
        }
    }

    @Override
    public void erasePost(Long postId, Long artistId) {
        postDAO.erasePost(postId, artistId);
    }
}
