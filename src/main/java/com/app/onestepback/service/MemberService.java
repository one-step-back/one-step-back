package com.app.onestepback.service;


import com.app.onestepback.domain.MemberVO;

import java.util.Optional;

public interface MemberService {

    //    회원가입 - 이메일
    public void join(MemberVO memberVO);

    //    이메일로 계정 조회
    public Optional<MemberVO> findAccount(String memberEmail);


}
