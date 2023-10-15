package com.app.onestepback.artist;

import com.app.onestepback.domain.ArtistPostDTO;
import com.app.onestepback.mapper.ArtistMapper;
import com.app.onestepback.mapper.ArtistPostMapper;
import com.app.onestepback.mapper.ArtistPostTagMapper;
import com.app.onestepback.mapper.VideoTagMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
@Slf4j
public class ArtistPostMapperTest {
    @Autowired
    private ArtistPostMapper artistPostMapper;
    @Autowired
    private ArtistPostTagMapper artistPostTagMapper;
    @Autowired
    private VideoTagMapper videoTagMapper;
    @Test
    public void selectAllTest(){
        artistPostMapper.select3Posts(1L).stream().map(ArtistPostDTO::toString).forEach(log::info);
    }

    @Test
    public void selectAllTagsTest(){
        artistPostTagMapper.selectAll(7L).stream().map(String::toString).forEach(log::info);
    }

    @Test
    public void videoTags(){
        videoTagMapper.selectAll(1L).stream().map(String::toString).forEach(log::info);
    }
}
