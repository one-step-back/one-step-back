package com.app.onestepback.mapper;

import com.app.onestepback.domain.MemberVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class MemberMapperTest {

    @Autowired
    private MemberMapper memberMapper;

    @Test
    void selectById() {
        MemberVO memberVO = memberMapper.selectById(1L);

        log.info(memberVO.toString());

    }





}