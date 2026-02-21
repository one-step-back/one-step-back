package com.app.onestepback.service.member;

import com.app.onestepback.domain.dto.member.MemberDTO;
import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.service.member.cmd.LoginWithEmailPasswordCmd;
import com.app.onestepback.service.member.cmd.MemberRegisterCmd;
import com.app.onestepback.service.member.cmd.MemberUpdateCmd;
import com.app.onestepback.service.member.cmd.PasswordUpdateCmd;

/**
 * 애플리케이션의 회원(Member) 관련 비즈니스 로직을 정의하는 서비스 인터페이스입니다.
 * <p>
 * 회원 가입, 로그인, 프로필 관리, 비밀번호 변경 및 계정 삭제(Soft Delete) 등의 기능을 제공합니다.
 * </p>
 */
public interface MemberService {

    /**
     * 자체 이메일 계정 기반의 신규 회원 가입을 처리합니다.
     *
     * @param cmd 회원 가입 처리에 필요한 사용자 입력 데이터 커맨드
     */
    void join(MemberRegisterCmd cmd);

    /**
     * 등록된 이메일과 비밀번호를 검증하여 로그인 처리를 수행하고 세션 객체를 반환합니다.
     *
     * @param cmd 이메일 및 비밀번호 정보가 담긴 커맨드 객체
     * @return 인증이 완료된 사용자 세션 전송 객체
     */
    SessionUser login(LoginWithEmailPasswordCmd cmd);

    /**
     * 특정 이메일이 시스템에 이미 가입되어 있는지 여부를 확인합니다.
     *
     * @param email 중복을 확인할 이메일 문자열
     * @return 가입되어 있을 경우 true, 그렇지 않을 경우 false
     */
    boolean existsByEmail(String email);

    /**
     * 회원의 프로필 정보(닉네임, 소개글, 이미지 등)를 수정합니다.
     *
     * @param cmd 프로필 업데이트에 필요한 정보가 담긴 커맨드 객체
     * @return 정보가 갱신된 최신 상태의 사용자 세션 객체
     */
    SessionUser updateProfile(MemberUpdateCmd cmd);

    /**
     * 회원의 상세 정보를 조회합니다.
     *
     * @param memberId 조회할 대상 회원의 고유 식별자
     * @return 회원의 상세 정보가 담긴 DTO
     */
    MemberDTO.Info getMemberInfo(Long memberId);

    /**
     * 회원의 비밀번호를 안전하게 변경합니다.
     * <p>
     * 기존 비밀번호가 존재하는 계정의 경우, 현재 비밀번호의 일치 여부를 반드시 검증합니다.
     * </p>
     *
     * @param cmd 비밀번호 변경에 필요한 현재/신규 비밀번호 데이터 커맨드
     */
    void updatePassword(PasswordUpdateCmd cmd);

    /**
     * 회원의 계정 상태를 '탈퇴(WITHDRAWN)'로 변경하여 논리적 삭제(Soft Delete)를 수행합니다.
     *
     * @param id 탈퇴 처리할 회원의 고유 식별자
     */
    void softDelete(Long id);
}