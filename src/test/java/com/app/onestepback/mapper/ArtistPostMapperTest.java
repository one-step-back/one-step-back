package com.app.onestepback.mapper;

import com.app.onestepback.domain.dto.artist.ArtistPostDTO;
import com.app.onestepback.domain.dto.artist.ArtistPostListDTO;
import com.app.onestepback.domain.vo.Pagination;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArtistPostMapperTest {
    @Autowired
    ArtistPostMapper artistPostMapper;

    @Test
    void selectAll() {
        Pagination pagination = new Pagination(2, 10, 12);
        List<ArtistPostListDTO> artistPostDTOS = artistPostMapper.selectAll(1L, pagination);

        System.out.println("artistPostDTOS = " + artistPostDTOS);
        System.out.println("artistPostDTOS = " + artistPostDTOS.size());
    }
}