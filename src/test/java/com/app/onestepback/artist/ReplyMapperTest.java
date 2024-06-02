package com.app.onestepback.artist;

import com.app.onestepback.mapper.PostReplyMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class ReplyMapperTest {
    @Autowired
    private PostReplyMapper postReplyMapper;

    @Test
    public void selectAllTest(){
//        Pagination pagination = new Pagination();
//        pagination.setStartRow(11);
//        pagination.setEndRow(20);
//
//        postReplyMapper.selectArtistPosts(101L, pagination).stream().map(PostReplyDTO::toString).forEach(log::info);
    }
}
