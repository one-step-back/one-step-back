package com.app.onestepback.service.member;

import com.app.onestepback.domain.vo.MemberVO;

import java.util.Optional;

public interface MemberService {

    //    회원가입
    void join(MemberVO memberVO, Long id);

    //    ID로 계정 조회
    MemberVO findById(Long id);

    //    이메일로 계정 조회
    Optional<MemberVO> findByEmail(String memberEmail);

    //    이메일로 로그인
    Optional<MemberVO> loginByEmail(MemberVO memberVO);

    //    카카오 프사 수정
    void updateKaKaoProfile(MemberVO memberVO);

    //    카카오 연동하기
    void synchronize(MemberVO memberVO);

    //    유저 복귀(MEMBER_STATUS = 'ACTIVE')
    void cancelWithdraw(Long id);

    //    유저 계정 삭제(MEMBER_STATUS = 'DISABLED')
    void softDeleteAccount(Long id);

    //    회원 삭제
    void delete(Long id);

    Optional<Long> checkArtist(Long memberId);

}
