package com.app.onestepback.web.api.v1.member;

import com.app.onestepback.domain.dto.member.MemberDTO;
import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.global.annotation.LoginUser;
import com.app.onestepback.global.common.response.ApiResponse;
import com.app.onestepback.service.member.MemberService;
import com.app.onestepback.service.member.cmd.MemberUpdateCmd;
import com.app.onestepback.web.api.v1.member.request.LoginWithEmailPasswordRequest;
import com.app.onestepback.web.api.v1.member.request.MemberPasswordUpdateRequest;
import com.app.onestepback.web.api.v1.member.request.MemberRegisterRequest;
import com.app.onestepback.web.api.v1.member.request.MemberUpdateRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberApiV1 {

    private final MemberService memberService;

    /**
     * 클라이언트로부터 전달받은 정보를 기반으로 자체 이메일 신규 회원 가입을 처리합니다.
     *
     * @param request 신규 회원 가입 데이터 (이메일, 비밀번호, 닉네임 등)
     * @return 처리 완료 응답 객체
     */
    @PostMapping("/join")
    public ResponseEntity<?> join(
            @Valid @ModelAttribute MemberRegisterRequest request
    ) {
        memberService.join(request.toCmd());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /**
     * 이메일과 비밀번호를 검증하여 로그인 처리를 수행하고 사용자 세션을 생성합니다.
     *
     * @param request 로그인 인증 데이터 (JSON)
     * @param session 인증된 사용자 정보를 보관할 HTTP 세션
     * @return 세션에 저장된 사용자 정보가 포함된 응답 객체
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<SessionUser>> login(
            @RequestBody @Valid LoginWithEmailPasswordRequest request,
            HttpSession session
    ) {
        SessionUser sessionUser = memberService.login(request.toCmd());
        session.setAttribute("member", sessionUser);

        return ResponseEntity.ok(ApiResponse.ok(sessionUser));
    }

    /**
     * 회원 가입 시 이메일의 중복 여부를 검증합니다.
     *
     * @param email 중복 검사할 이메일 문자열
     * @return 사용 가능 여부 (true: 사용 가능, false: 중복)
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestParam("email") String email) {
        boolean isExists = memberService.existsByEmail(email);
        return ResponseEntity.ok(ApiResponse.ok(!isExists));
    }

    /**
     * 특정 회원의 상세 프로필 정보를 조회합니다.
     *
     * @param memberId 조회할 회원의 고유 식별자
     * @return 회원 프로필 상세 정보 DTO
     */
    @GetMapping("/info/{memberId}")
    public ResponseEntity<?> getMemberInfo(@PathVariable("memberId") Long memberId) {
        MemberDTO.Info memberInfo = memberService.getMemberInfo(memberId);
        return ResponseEntity.ok(ApiResponse.ok(memberInfo));
    }

    /**
     * 로그인한 사용자의 프로필 정보(닉네임, 소개글, 이미지)를 수정하고 세션을 동기화합니다.
     *
     * @param sessionUser 현재 인가된 사용자의 세션 객체
     * @param body        수정할 폼 데이터
     * @param session     정보 동기화를 수행할 HTTP 세션
     * @return 처리 완료 응답 객체
     */
    @PatchMapping("/update")
    public ResponseEntity<?> updateMember(
            @LoginUser SessionUser sessionUser,
            @Valid @ModelAttribute MemberUpdateRequest body,
            HttpSession session
    ) {
        MemberUpdateCmd cmd = new MemberUpdateCmd(sessionUser.getId(), body.nickname(), body.introduction(), body.profileImage());
        SessionUser updatedSessionUser = memberService.updateProfile(cmd);

        session.setAttribute("member", updatedSessionUser);

        return ResponseEntity.ok(ApiResponse.ok());
    }

    /**
     * 로그인한 사용자의 비밀번호를 변경하고 보안을 위해 기존 세션을 만료 처리합니다.
     *
     * @param sessionUser 현재 인가된 사용자의 세션 객체
     * @param body        현재 비밀번호 및 신규 비밀번호 데이터 (JSON)
     * @param session     만료시킬 HTTP 세션
     * @return 처리 완료 응답 객체
     */
    @PatchMapping("/password")
    public ResponseEntity<?> newPassword(
            @LoginUser SessionUser sessionUser,
            @RequestBody @Valid MemberPasswordUpdateRequest body,
            HttpSession session
    ) {
        memberService.updatePassword(body.toCmd(sessionUser.getId()));
        session.invalidate();

        return ResponseEntity.ok(ApiResponse.ok());
    }

    /**
     * 로그인한 사용자의 계정을 탈퇴(논리적 삭제) 처리하고 세션을 즉시 파기합니다.
     *
     * @param sessionUser 현재 인가된 사용자의 세션 객체
     * @param session     파기할 HTTP 세션
     * @return 성공 메시지 문자열
     */
    @DeleteMapping("/withdraw")
    public ResponseEntity<?> withdraw(
            @LoginUser SessionUser sessionUser,
            HttpSession session
    ) {
        memberService.softDelete(sessionUser.getId());
        session.invalidate();

        return ResponseEntity.ok("회원 탈퇴가 처리되었습니다.");
    }
}