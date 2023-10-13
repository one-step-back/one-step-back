package com.app.onestepback.service;

import com.app.onestepback.repository.MemberDAO;
import com.app.onestepback.domain.MemberVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class MemberServiceImpl implements MemberService{

    private final MemberDAO memberDAO;

    //    회원가입 - 이메일
    @Override
    public void join(MemberVO memberVO) {
        memberDAO.enroll(memberVO);
    }

}
