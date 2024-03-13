package com.app.onestepback.repository;

import com.app.onestepback.domain.vo.MemberVO;
import com.app.onestepback.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberDAO {
    private final MemberMapper memberMapper;

    //    회원가입
    public void enroll(MemberVO memberVO) {
        memberMapper.insert(memberVO);
    }

    //    ID로 계정 조회
    public MemberVO findById(Long id){
        return memberMapper.selectById(id);
    }

    //    이메일로 계정 조회
    public Optional<MemberVO> findByEmail(String memberEmail) {
        return memberMapper.selectByEmail(memberEmail);
    }

    //    이메일로 로그인
    public Optional<MemberVO> loginByEmail(MemberVO memberVO) {
        return memberMapper.selectByEmailAndPassword(memberVO);
    }

    //    카카오 프사 수정
    public void setKaKaoProfile(MemberVO memberVO){
        memberMapper.updateKaKaoProfile(memberVO);
    }

    //    카카오 연동하기
    public void updateBySync(MemberVO memberVO){
        memberMapper.updateBySync(memberVO);
    }

    //    유저 복귀(MEMBER_STATUS = 'ACTIVE')
    public void setStatusActive(Long id) {
        memberMapper.updateToActive(id);
    }

    //    유저 계정 삭제(MEMBER_STATUS = 'DISABLED')
    public void setStatusDisabled(Long id) {
        memberMapper.updateToDelete(id);
    }

    //    회원 삭제
    public void delete(Long id){
        memberMapper.delete(id);
    }

    //    마이페이지(프로필) 수정(닉네임, 자기소개서)
    public void updateByNicknameIntroduction(MemberVO memberVO){
        memberMapper.updateNicknameIntroduction(memberVO);
    }

}
