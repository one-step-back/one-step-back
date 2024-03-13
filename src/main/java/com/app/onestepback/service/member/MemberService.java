package com.app.onestepback.service.member;


import com.app.onestepback.domain.vo.MemberVO;

import java.util.Optional;

public interface MemberService {

    //    회원가입
    public void join(MemberVO memberVO, Long id);

    //    ID로 계정 조회
    public MemberVO findById(Long id);

    //    이메일로 계정 조회
    public Optional<MemberVO> findByEmail(String memberEmail);

    //    이메일로 로그인
    public Optional<MemberVO> loginByEmail(MemberVO memberVO);

    //    카카오 프사 수정
    public void updateKaKaoProfile(MemberVO memberVO);

    //    카카오 연동하기
    public void synchronize(MemberVO memberVO);

    //    유저 복귀(MEMBER_STATUS = 'ACTIVE')
    public void cancelWithdraw(Long id);

    //    유저 계정 삭제(MEMBER_STATUS = 'DISABLED')
    public void softDeleteAccount(Long id);

    //    회원 삭제
    public void delete(Long id);

    public Optional<Long> checkArtist(Long memberId);

}
