package com.app.onestepback.repository;

import com.app.onestepback.domain.MemberVO;
import com.app.onestepback.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberDAO {
    private final MemberMapper memberMapper;

    //    회원가입 - 이메일
    public void enroll(MemberVO memberVO) {
        memberMapper.insert(memberVO);
    }

}
