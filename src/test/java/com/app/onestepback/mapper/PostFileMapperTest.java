package com.app.onestepback.mapper;

import com.app.onestepback.domain.dto.postElements.PostFileDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class PostFileMapperTest {

    @Autowired
    private PostFileMapper postFileMapper;

    @Test
    void selectAll() {
        List<PostFileDTO> postFileDTOS = postFileMapper.selectAll(31L);

        System.out.println("postFileDTOS = " + postFileDTOS);
    }
}