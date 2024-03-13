package com.app.onestepback.service.member;

import com.app.onestepback.repository.ArtistDAO;
import com.app.onestepback.repository.MemberDAO;
import com.app.onestepback.domain.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class MemberServiceImpl implements MemberService{
    private final MemberDAO memberDAO;
    private final ArtistDAO artistDAO;

    //    회원가입
    @Override
    public void join(MemberVO memberVO, Long id) {
//      1. 일반회원, 카카오회원 구분
        if(memberVO.getMemberKakaoProfileUrl() != null) { //카카오 로그인
            Optional<MemberVO> foundMember = findByEmail(memberVO.getMemberEmail());
            if(id != null) {
                memberVO.setId(id);
//                카카오 연동 시 기존 정보를 지우면서 이메일 로그인이 안 되기 때문에 delete를 없애고 update만 하게 수정
//                if(foundMember.get().getMemberPassword() != null) {
//                    memberVO.setMemberPassword(foundMember.get().getMemberPassword());
//                }
//                delete(foundMember.get().getId());
                synchronize(memberVO);
                return;
            }
//          1-2. 최초 로그인 검사
            if(foundMember.isEmpty()) { // 이메일 정보가 없을 경우 회원가입
                memberDAO.enroll(memberVO);
            } else { // 계정이 존재하면 로그인
                MemberVO member = foundMember.get();
//              1-3. 카카오회원일 경우 프사 검사
                if(member.getMemberKakaoProfileUrl() == null){ // 전달받은 프사경로로 수정
//                  전달받은 카카오 프사로 업데이트
                    updateKaKaoProfile(memberVO);
                }
            }
        } else {
            memberDAO.enroll(memberVO);
        }
    }

    //    ID로 계정 조회
    @Override
    public MemberVO findById(Long id) {
        return memberDAO.findById(id);
    }

    //    이메일로 계정 조회
    @Override
    public Optional<MemberVO> findByEmail(String memberEmail) {
        return memberDAO.findByEmail(memberEmail);
    }

    //    이메일로 로그인
    @Override
    public Optional<MemberVO> loginByEmail(MemberVO memberVO) {
        return memberDAO.loginByEmail(memberVO);
    }

    //    카카오 프사 수정
    @Override
    public void updateKaKaoProfile(MemberVO memberVO) {
        memberDAO.setKaKaoProfile(memberVO);
    }

    //    카카오 연동하기
    @Override
    public void synchronize(MemberVO memberVO) {
        memberDAO.updateBySync(memberVO);
    }

    //    유저 복귀(MEMBER_STATUS = 'ACTIVE')
    @Override
    public void cancelWithdraw(Long id) {
        memberDAO.setStatusActive(id);
    }

    //    유저 계정 삭제(MEMBER_STATUS = 'DISABLED')
    @Override
    public void softDeleteAccount(Long id) {
        memberDAO.setStatusDisabled(id);
    }

    //    회원 삭제 - 카카오 연동
    @Override
    public void delete(Long id) {
        memberDAO.delete(id);
    }

    //    아티스트 여부 확인
    @Override
    public Optional<Long> checkArtist(Long memberId) {
        return artistDAO.checkArtist(memberId);
    }
}
