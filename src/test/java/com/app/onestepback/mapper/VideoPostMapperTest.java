package com.app.onestepback.mapper;

import com.app.onestepback.domain.dto.artist.video.ArtistVideoDetailDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class VideoPostMapperTest {
    @Autowired
    VideoPostMapper videoPostMapper;

    @Test
    void select() {
        Optional<ArtistVideoDetailDTO> select = videoPostMapper.select(1L, 61L, 2L);

        System.out.println("select = " + select.get());
    }
}