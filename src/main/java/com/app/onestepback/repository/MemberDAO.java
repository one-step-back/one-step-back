package com.app.onestepback.repository;

import com.app.onestepback.domain.MemberVO;
import com.app.onestepback.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberDAO {
    private final MemberMapper memberMapper;

    //    회원가입 - 이메일
    public void enroll(MemberVO memberVO) {
        memberMapper.insert(memberVO);
    }

    //    이메일로 계정 조회
    public Optional<MemberVO> findByEmail(String memberEmail) {
        return memberMapper.selectByEmail(memberEmail);
    }

}
