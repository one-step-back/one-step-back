package com.app.onestepback.artist;

import com.app.onestepback.mapper.ArtistMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class artistMapperTest {
    @Autowired
    private ArtistMapper artistMapper;

    @Test
    public void selectTest(){
        log.info(String.valueOf(artistMapper.selectAM(1L)));
    }
}
