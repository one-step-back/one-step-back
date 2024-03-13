package com.app.onestepback.service.mypage;

import com.app.onestepback.domain.vo.MemberVO;
import com.app.onestepback.repository.MemberDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class MyPageServiceImpl implements MyPageService {
    private final MemberDAO memberDAO;

//    id로 계정 조회
    @Override
    public MemberVO findById(Long id) {
        return memberDAO.findById(id);
    }

    //    마이페이지(프로필) 수정(닉네임, 자기소개서)
    @Override
    public void modifyByNicknameIntroduction(MemberVO memberVO) {
        memberDAO.updateByNicknameIntroduction(memberVO);
    }

}
