package com.app.onestepback.mapper;

import com.app.onestepback.domain.vo.MemberVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface MemberMapper {

    //    회원가입
    public void insert(MemberVO memberVO);

    //    ID로 계정 조회
    public MemberVO selectById(Long id);

    //    이메일로 계정 조회
    public Optional<MemberVO> selectByEmail(String memberEmail);

    //    이메일 로그인
    public Optional<MemberVO> selectByEmailAndPassword(MemberVO memberVO);

    //    유저 복귀(MEMBER_STATUS = 'ACTIVE')
    public void updateToActive(Long id);

    //    유저 계정 삭제(MEMBER_STATUS = 'DISABLED')
    public void updateToDelete(Long id);

    //    카카오 프사 수정
    public void updateKaKaoProfile(MemberVO memberVO);

    //    카카오 연동하기
    public void updateBySync(MemberVO memberVO);

    //    마이페이지(프로필) 수정(닉네임, 자기소개서)
    public void updateNicknameIntroduction(MemberVO memberVO);

    //    회원 삭제
    public void delete(Long id);
}
