package com.app.onestepback.web.api.v1.artist;

import com.app.onestepback.domain.dto.artist.ArtistBankAccountDTO;
import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.global.annotation.LoginUser;
import com.app.onestepback.global.common.response.ApiResponse;
import com.app.onestepback.service.artist.ArtistService;
import com.app.onestepback.service.bank.BankVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class ArtistAccountApiV1 {

    private final ArtistService artistService;
    private final BankVerificationService bankVerificationService;

    /**
     * @description 로그인한 아티스트의 정산용 계좌 정보를 조회합니다.
     *
     * @param user 현재 인가된 아티스트의 세션 사용자 정보
     * @return 아티스트의 정산 계좌 정보가 포함된 표준 응답 객체
     */
    @GetMapping
    public ResponseEntity<ApiResponse<ArtistBankAccountDTO>> getAccount(
            @LoginUser(artistOnly = true) SessionUser user
    ) {
        ArtistBankAccountDTO account = artistService.getBankAccount(user.getId());
        return ResponseEntity.ok(ApiResponse.ok(account));
    }

    /**
     * @description 입력된 계좌 정보의 예금주 실명을 외부 뱅킹 API를 통해 검증하고, 유효한 경우 시스템에 저장합니다.
     *
     * @param user 현재 인가된 아티스트의 세션 사용자 정보
     * @param body 검증 및 저장할 은행 코드, 계좌 번호, 예금주 실명 데이터
     * @return 비즈니스 로직 성공 여부 및 처리 결과 메시지가 포함된 표준 응답 객체
     */
    @PostMapping
    public ResponseEntity<ApiResponse<String>> verifyAndSaveAccount(
            @LoginUser(artistOnly = true) SessionUser user,
            @RequestBody ArtistBankAccountDTO body
    ) {
        boolean isValid = bankVerificationService.verifyAccountHolder(
                body.getBankCode().name(),
                body.getAccountNumber(),
                body.getAccountHolder()
        );

        if (!isValid) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.fail("INVALID_ACCOUNT_INFO", "계좌 정보가 일치하지 않거나 유효하지 않습니다.")
            );
        }

        artistService.updateAccount(user.getId(), body);

        return ResponseEntity.ok(ApiResponse.ok());
    }
}