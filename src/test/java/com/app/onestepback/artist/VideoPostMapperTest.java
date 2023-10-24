package com.app.onestepback.artist;

import com.app.onestepback.domain.Pagination;
import com.app.onestepback.domain.VideoPostDTO;
import com.app.onestepback.mapper.VideoPostMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class VideoPostMapperTest {
    @Autowired
    private VideoPostMapper videoPostMapper;

    @Test
    public void selectAllTest(){
        Pagination pagination = new Pagination();

        pagination.setStartRow(1);
        pagination.setEndRow(10);
        videoPostMapper.selectAll(1L, pagination).stream().map(VideoPostDTO::toString).forEach(log::info);
    }


}
