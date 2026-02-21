package com.app.onestepback.repository;

import com.app.onestepback.domain.dto.member.MemberDTO;
import com.app.onestepback.domain.model.MemberVO;
import com.app.onestepback.domain.type.member.MemberStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface MemberMapper {
    //    회원가입
    void insert(MemberVO memberVO);

    Optional<MemberVO> selectById(Long memberId);

    //    이메일로 계정 조회
    Optional<MemberVO> selectByEmail(String memberEmail);

    boolean existsByEmail(String email);

    Optional<MemberDTO.Info> selectInfoById(@Param("id") Long memberId);

    void updateKakao(@Param("id") Long id,
                     @Param("kakaoId") Long kakaoId,
                     @Param("profileUrl") String profileUrl);

    void updateProfileId(Long memberId, String memberProfileId);

    void updateMember(MemberVO memberVO);

    void updatePassword(Long memberId, String newPassword);

    void updateStatus(@Param("id") Long id, @Param("status") MemberStatus status);

    void updatePaymentTotal(@Param("memberId") Long memberId, @Param("amount") Long amount);

    //    유저 복귀(MEMBER_STATUS = 'ACTIVE')
    void updateToActive(Long id);

    //    유저 계정 삭제(MEMBER_STATUS = 'DISABLED')
    void updateToDelete(Long id);

    //    카카오 프사 수정
    void updateKaKaoProfile(MemberVO memberVO);

    //    카카오 연동하기
    void updateBySync(MemberVO memberVO);

    //    마이페이지(프로필) 수정(닉네임, 자기소개서)
    void updateNicknameIntroduction(MemberVO memberVO);

    //    회원 삭제
    void delete(Long id);
}
