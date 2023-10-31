package com.app.onestepback.service;

import com.app.onestepback.domain.MemberVO;

public interface MyPageService {

//    id로 계정 조회
    public MemberVO findById(Long id);

    //    마이페이지(프로필) 수정(닉네임, 자기소개서)
    public void modifyByNicknameIntroduction(MemberVO memberVO);

}
