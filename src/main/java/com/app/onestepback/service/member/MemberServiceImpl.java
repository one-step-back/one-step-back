package com.app.onestepback.service.member;

import com.app.onestepback.domain.dto.member.MemberDTO;
import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.domain.model.MemberVO;
import com.app.onestepback.domain.type.member.MemberStatus;
import com.app.onestepback.global.exception.BusinessException;
import com.app.onestepback.global.exception.ErrorCode;
import com.app.onestepback.global.util.PasswordEncoder;
import com.app.onestepback.repository.MemberMapper;
import com.app.onestepback.service.file.FileInfo;
import com.app.onestepback.service.file.FileService;
import com.app.onestepback.service.file.cmd.SaveFileCmd;
import com.app.onestepback.service.member.cmd.LoginWithEmailPasswordCmd;
import com.app.onestepback.service.member.cmd.MemberRegisterCmd;
import com.app.onestepback.service.member.cmd.MemberUpdateCmd;
import com.app.onestepback.service.member.cmd.PasswordUpdateCmd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 애플리케이션 회원 비즈니스 로직을 처리하는 서비스 구현체입니다.
 * <p>
 * 글로벌 예외 처리기(Advice)와 연동하기 위해 시스템 전역의 ErrorCode를 활용한
 * BusinessException을 명시적으로 발생시킵니다.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final FileService fileService;
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void join(MemberRegisterCmd cmd) {
        if (memberMapper.selectByEmail(cmd.email()).isPresent()) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATE);
        }

        String encodedPassword = passwordEncoder.encode(cmd.rawPassword());

        MemberVO memberVO = MemberVO.builder()
                .memberEmail(cmd.email())
                .memberPassword(encodedPassword)
                .memberNickname(cmd.nickname())
                .memberIntroduction(cmd.introduction())
                .build();

        memberMapper.insert(memberVO);

        processProfileImage(memberVO.getId(), cmd.profileImage());
    }

    /**
     * 회원의 프로필 이미지를 파일 스토리지에 저장하고 데이터베이스를 갱신합니다.
     */
    private void processProfileImage(Long memberId, MultipartFile profileImage) {
        if (profileImage == null || profileImage.isEmpty()) {
            return;
        }

        SaveFileCmd saveCmd = new SaveFileCmd(
                memberId,
                memberId,
                "PROFILE",
                profileImage.getOriginalFilename(),
                profileImage.getContentType(),
                profileImage.getSize(),
                () -> {
                    try {
                        return profileImage.getInputStream();
                    } catch (IOException e) {
                        log.error("[Member Service] 신규 프로필 이미지 스트림 추출 중 오류가 발생하였습니다. MemberID: {}", memberId, e);
                        throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR);
                    }
                }
        );

        FileInfo fileInfo = fileService.store(saveCmd);
        memberMapper.updateProfileId(memberId, fileInfo.id());
    }

    @Override
    @Transactional(readOnly = true)
    public SessionUser login(LoginWithEmailPasswordCmd cmd) {
        MemberVO member = memberMapper.selectByEmail(cmd.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.LOGIN_FAILED));

        if (member.getMemberPassword() == null ||
                passwordEncoder.isMismatch(cmd.password(), member.getMemberPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        return new SessionUser(member);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return memberMapper.existsByEmail(email);
    }

    @Override
    @Transactional
    public SessionUser updateProfile(MemberUpdateCmd cmd) {
        String profileImageId = null;

        if (cmd.profileImage() != null && !cmd.profileImage().isEmpty()) {
            MultipartFile profileImage = cmd.profileImage();
            SaveFileCmd saveCmd = new SaveFileCmd(
                    cmd.memberId(),
                    cmd.memberId(),
                    "PROFILE",
                    profileImage.getOriginalFilename(),
                    profileImage.getContentType(),
                    profileImage.getSize(),
                    () -> {
                        try {
                            return profileImage.getInputStream();
                        } catch (IOException e) {
                            log.error("[Member Service] 프로필 업데이트 중 이미지 스트림 추출 오류가 발생하였습니다. MemberID: {}", cmd.memberId(), e);
                            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR);
                        }
                    }
            );

            FileInfo fileInfo = fileService.store(saveCmd);
            profileImageId = fileInfo.id();
        }

        MemberVO updateVO = MemberVO.builder()
                .id(cmd.memberId())
                .memberNickname(cmd.nickname())
                .memberIntroduction(cmd.introduction())
                .memberProfileId(profileImageId)
                .build();

        memberMapper.updateMember(updateVO);

        MemberVO updatedMember = memberMapper.selectById(cmd.memberId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        return new SessionUser(updatedMember);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDTO.Info getMemberInfo(Long memberId) {
        return memberMapper.selectInfoById(memberId).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND)
        );
    }

    @Override
    @Transactional
    public void updatePassword(PasswordUpdateCmd cmd) {
        MemberVO member = memberMapper.selectById(cmd.memberId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND)
        );

        if (member.getMemberPassword() != null) {
            if (cmd.currentPassword() == null || cmd.currentPassword().isBlank()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
            }

            if (passwordEncoder.isMismatch(cmd.currentPassword(), member.getMemberPassword())) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
            }
        }

        String encodedPwd = passwordEncoder.encode(cmd.newPassword());
        memberMapper.updatePassword(cmd.memberId(), encodedPwd);
    }

    @Override
    @Transactional
    public void softDelete(Long id) {
        memberMapper.updateStatus(id, MemberStatus.WITHDRAWN);
    }
}