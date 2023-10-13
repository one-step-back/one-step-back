package com.app.onestepback.service;

import com.app.onestepback.repository.MemberDAO;
import com.app.onestepback.domain.MemberVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class MemberServiceImpl implements MemberService{
    private final MemberDAO memberDAO;

    //    회원가입 - 이메일
    @Override
    public void join(MemberVO memberVO) {
        if (findAccount(memberVO.getMemberEmail()).isEmpty()) {
            memberDAO.enroll(memberVO);
        }
    }

    //    이메일로 계정 조회
    @Override
    public Optional<MemberVO> findAccount(String memberEmail) {
        return memberDAO.findByEmail(memberEmail);
    }

    //    이메일로 로그인
    @Override
    public Optional<MemberVO> loginByEmail(String memberEmail, String memberPassword) {
        return memberDAO.loginByEmail(memberEmail, memberPassword);
    }


}
