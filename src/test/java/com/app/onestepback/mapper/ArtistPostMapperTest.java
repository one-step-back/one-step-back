package com.app.onestepback.mapper;

import com.app.onestepback.domain.dto.artist.post.ArtistPostDetailDTO;
import com.app.onestepback.domain.dto.artist.post.ArtistPostListDTO;
import com.app.onestepback.domain.type.post.PostSortType;
import com.app.onestepback.domain.vo.Pagination;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.NoSuchElementException;

@SpringBootTest
class ArtistPostMapperTest {
    @Autowired
    ArtistPostMapper artistPostMapper;

    @Test
    void selectAll() {
        List<ArtistPostListDTO> content = artistPostMapper.selectAll(PostSortType.LIKE_DESC);

        System.out.println("content = " + content);
    }

//    @Test
//    void selectArtistPosts() {
//        Pagination pagination = new Pagination(2, 10, 12);
//        List<ArtistPostListDTO> artistPostDTOS = artistPostMapper.selectArtistPosts(1L, pagination);
//
//        System.out.println("artistPostDTOS = " + artistPostDTOS);
//        System.out.println("artistPostDTOS = " + artistPostDTOS.size());
//    }
//
//    @Test
//    void selectTest() {
//        ArtistPostDetailDTO artistPostDetailDTO = artistPostMapper.select(1L, 14L).orElseThrow(
//                NoSuchElementException::new
//        );
//
//        System.out.println("artistPostDetailDTO = " + artistPostDetailDTO);
//    }
}