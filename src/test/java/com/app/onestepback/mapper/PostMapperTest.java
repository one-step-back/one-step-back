package com.app.onestepback.mapper;

import com.app.onestepback.domain.dto.PostDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor
@Slf4j
class PostMapperTest {
    @Autowired
    private PostMapper postMapper;

    @Test
    void selectPosts() {
        List<PostDTO> postDTOS = postMapper.selectPosts(3);

        log.info(postDTOS.toString());
    }
}