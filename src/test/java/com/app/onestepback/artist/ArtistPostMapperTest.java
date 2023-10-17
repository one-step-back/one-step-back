package com.app.onestepback.artist;

import com.app.onestepback.domain.ArtistPostDTO;
import com.app.onestepback.domain.Pagination;
import com.app.onestepback.mapper.ArtistPostMapper;
import com.app.onestepback.mapper.ArtistPostTagMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class ArtistPostMapperTest {
    @Autowired
    private ArtistPostMapper artistPostMapper;
    @Test
    public void selectAllTest(){
        artistPostMapper.select3Posts(1L).stream().map(ArtistPostDTO::toString).forEach(log::info);
    }

    @Test
    public void paginationTest(){
        Pagination pagination = new Pagination();
        pagination.setStartRow(1);
        pagination.setEndRow(10);
        artistPostMapper.selectAll(1L, pagination).stream().map(ArtistPostDTO::toString).forEach(log::info);
    }
}
