package com.app.onestepback.artist;

import com.app.onestepback.service.artist.ArtistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class ArtistServiceTest {
    @Autowired
    private ArtistService artistService;

}
