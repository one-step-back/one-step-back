package com.app.onestepback.artist;

import com.app.onestepback.service.ArtistService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class ArtistServiceTest {
    @Autowired
    private ArtistService artistService;

    @Test
    public void getCountOfSubscriberTest(){
        log.info(String.valueOf(artistService.getCountOfSubscriber(1L)));
    }

    @Test
    public void getCountOfPostTest(){
        log.info(String.valueOf(artistService.getCountOfPost(1L)));
    }
}
