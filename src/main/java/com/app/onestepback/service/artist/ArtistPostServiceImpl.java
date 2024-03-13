package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.ArtistDTO;
import com.app.onestepback.domain.dto.ArtistPostDTO;
import com.app.onestepback.domain.vo.Pagination;
import com.app.onestepback.domain.vo.PostFileVO;
import com.app.onestepback.domain.vo.PostTagVO;
import com.app.onestepback.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArtistPostServiceImpl implements ArtistPostService {
    private final ArtistDAO artistDAO;
    private final ArtistPostDAO artistPostDAO;
    private final PostTagDAO postTagDAO;
    private final PostFileDAO postFileDAO;
    private final PostDAO postDAO;

    @Override
    public Optional<ArtistDTO> getArtist(Long memberId) {
        return artistDAO.getArtist(memberId);
    }

    @Override
    public int getPostCount(Long memberId) {
        return artistPostDAO.getCountOfPost(memberId);
    }

    @Override
    public List<ArtistPostDTO> getAllPosts(Long memberId, Pagination pagination) {
        List<ArtistPostDTO> posts = artistPostDAO.getAllPosts(memberId, pagination);

        for (ArtistPostDTO post : posts) {
            List<String> tags = postTagDAO.getAllTags(post.getId());

            if (!tags.isEmpty()) {
                post.setTag1(tags.get(0));
            }
            if (tags.size() >= 2) {
                post.setTag2(tags.get(1));
            }
            if (tags.size() >= 3) {
                post.setTag3(tags.get(2));
            }
            if (tags.size() >= 4) {
                post.setTag4(tags.get(3));
            }
            if (tags.size() >= 5) {
                post.setTag5(tags.get(4));
            }
        }
        return posts;
    }

    @Override
    public List<String> getAllTags(Long postId) {
        return postTagDAO.getAllTags(postId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePost(ArtistPostDTO artistPostDTO, int numberOfTags, List<String> uuids, List<MultipartFile> uploadFiles) {
        artistPostDAO.savePost(artistPostDTO);
        artistPostDAO.saveArtistPost(artistPostDTO.getId());

        for (int i = 1; i <= numberOfTags; i++) {
            try {
                PostTagVO postTagVO = new PostTagVO();

                String tagName = "tag" + i;
                Field field = artistPostDTO.getClass().getDeclaredField(tagName);
                field.setAccessible(true);

                postTagVO.setPostId(artistPostDTO.getId());
                postTagVO.setPostTagName((String) field.get(artistPostDTO));

                postTagDAO.savePostTag(postTagVO);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (uuids != null) {
            for (int i = 0; i < uploadFiles.size(); i++) {
                PostFileVO postFileVO = new PostFileVO();

                postFileVO.setPostId(artistPostDTO.getId());
                postFileVO.setFileName(uuids.get(i) + "_" + uploadFiles.get(i).getOriginalFilename());
                postFileVO.setFilePath(getPath());

                postFileDAO.saveFile(postFileVO);
            }
        }
    }

    @Override
    public ArtistPostDTO getPost(Long id) {
        postDAO.viewCountUp(id);
        ArtistPostDTO post = artistPostDAO.getPost(id);

        List<String> tags = postTagDAO.getAllTags(post.getId());

        if (!tags.isEmpty()) {
            post.setTag1(tags.get(0));
        }
        if (tags.size() >= 2) {
            post.setTag2(tags.get(1));
        }
        if (tags.size() >= 3) {
            post.setTag3(tags.get(2));
        }
        if (tags.size() >= 4) {
            post.setTag4(tags.get(3));
        }
        if (tags.size() >= 5) {
            post.setTag5(tags.get(4));
        }

        return post;
    }

    @Override
    public Optional<ArtistPostDTO> getPrevPost(ArtistPostDTO artistPostDTO) {
        return artistPostDAO.getPrevPost(artistPostDTO);
    }

    @Override
    public Optional<ArtistPostDTO> getNextPost(ArtistPostDTO artistPostDTO) {
        return artistPostDAO.getNextPost(artistPostDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editPost(ArtistPostDTO artistPostDTO, int numberOfTags, List<String> uuids, List<MultipartFile> uploadFiles) {
        artistPostDAO.editPost(artistPostDTO);

        postTagDAO.deletePostTag(artistPostDTO.getId());
        for (int i = 1; i <= numberOfTags; i++) {
            try {
                PostTagVO postTagVO = new PostTagVO();

                String tagName = "tag" + i;
                Field field = artistPostDTO.getClass().getDeclaredField(tagName);
                field.setAccessible(true);

                postTagVO.setPostId(artistPostDTO.getId());
                postTagVO.setPostTagName((String) field.get(artistPostDTO));

                postTagDAO.savePostTag(postTagVO);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (uuids != null) {
            for (int i = 0; i < uploadFiles.size(); i++) {
                PostFileVO postFileVO = new PostFileVO();

                postFileVO.setPostId(artistPostDTO.getId());
                postFileVO.setFileName(uuids.get(i) + "_" + uploadFiles.get(i).getOriginalFilename());
                postFileVO.setFilePath(getPath());

                postFileDAO.saveFile(postFileVO);
            }
        }
    }

    @Override
    public List<PostFileVO> getAllFiles(Long postId) {
        return postFileDAO.getAllFiles(postId);
    }

    @Override
    public void erasePost(Long id) {
        postDAO.erasePost(id);
    }


    private String getPath() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }
}
