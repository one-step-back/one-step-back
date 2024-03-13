package com.app.onestepback.artist;

import com.app.onestepback.domain.dto.CombinedPostDTO;
import com.app.onestepback.mapper.CombinedPostMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class CombinedPostMapperTest {
    @Autowired
    private CombinedPostMapper combinedPostMapper;

    @Test
    public void selectTest(){
        combinedPostMapper.select5Posts().stream().map(CombinedPostDTO::toString).forEach(log::info);
    }
}
