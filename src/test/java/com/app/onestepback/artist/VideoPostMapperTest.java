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


    @Test
    public void selectTest(){
        log.info(videoPostMapper.select(269L).toString());
    }

    @Test
    public void selectPrev(){
        VideoPostDTO videoPostDTO = new VideoPostDTO();

        videoPostDTO.setId(224L);
        videoPostDTO.setPostWriteTime("2023-10-24 04:24:19.250075");
        videoPostDTO.setMemberId(1L);

        log.info(videoPostMapper.selectPrevPost(videoPostDTO).toString());
    }
}
