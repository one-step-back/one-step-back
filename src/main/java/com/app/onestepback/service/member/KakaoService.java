package com.app.onestepback.service.member;

import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.domain.dto.oauth.KakaoTokenResponse;
import com.app.onestepback.domain.dto.oauth.KakaoUserInfoResponse;
import com.app.onestepback.domain.model.MemberVO;
import com.app.onestepback.global.config.KakaoConfig;
import com.app.onestepback.global.exception.BusinessException;
import com.app.onestepback.global.exception.ErrorCode;
import com.app.onestepback.repository.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * 카카오(Kakao) OAuth2 기반의 인증 및 회원 동기화 비즈니스 로직을 처리하는 서비스 컴포넌트입니다.
 * <p>
 * 카카오 인증 서버와의 토큰 교환, 리소스 서버로부터의 프로필 데이터 수집 및
 * 내부 시스템 데이터베이스와의 계정 연동(신규 가입 또는 기존 계정 매핑)을 총괄합니다.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    private final MemberMapper memberMapper;
    private final RestTemplate restTemplate;
    private final KakaoConfig kakaoConfig;

    private record KakaoTokenRequest(
            String grantType,
            String clientId,
            String redirectUri,
            String code,
            String clientSecret
    ) {
        public String toUrlEncodedString() {
            return "grant_type=" + grantType +
                    "&client_id=" + clientId +
                    "&redirect_uri=" + redirectUri +
                    "&code=" + code +
                    "&client_secret=" + clientSecret;
        }
    }

    /**
     * 카카오 인증 서버로부터 전달받은 인가 코드를 이용하여 로그인을 수행합니다.
     *
     * @param code 카카오 로그인 완료 후 콜백으로 전달된 인가 코드(Authorization Code)
     * @return 내부 시스템에서 인증된 사용자 세션 객체
     */
    @Transactional
    public SessionUser login(String code) {
        String accessToken = getAccessToken(code);
        KakaoUserInfoResponse userInfo = getUserInfo(accessToken);

        MemberVO member = syncMember(userInfo);
        return new SessionUser(member);
    }

    /**
     * 카카오 사용자 정보를 기반으로 내부 시스템의 회원 데이터를 동기화합니다.
     * <p>
     * 이메일을 기준으로 기존 회원이 존재하나 카카오 ID가 매핑되지 않은 경우 연동을 수행하며,
     * 정보가 존재하지 않을 경우 새로운 카카오 가입 회원으로 시스템에 등록합니다.
     * </p>
     *
     * @param userInfo 카카오 리소스 서버로부터 반환받은 사용자 프로필 데이터
     * @return 동기화가 완료된 회원 엔티티(VO)
     */
    private MemberVO syncMember(KakaoUserInfoResponse userInfo) {
        String email = userInfo.getKakaoAccount().getEmail();
        Long kakaoId = userInfo.getId();
        String nickname = userInfo.getKakaoAccount().getProfile().getNickname();
        String profileImage = userInfo.getKakaoAccount().getProfile().getProfileImageUrl();

        Optional<MemberVO> memberOpt = memberMapper.selectByEmail(email);

        if (memberOpt.isPresent()) {
            MemberVO member = memberOpt.get();

            if (member.getMemberKakaoId() == null) {
                log.info("[OAuth2 Kakao] 기존 계정에 카카오 인증 정보를 연동합니다. Email: {}", email);
                memberMapper.updateKakao(member.getId(), kakaoId, profileImage);
                member.setKakao(kakaoId, profileImage);
            }

            return member;
        } else {
            log.info("[OAuth2 Kakao] 신규 카카오 연동 회원을 등록합니다. Email: {}", email);
            MemberVO newMember = MemberVO.builder()
                    .memberEmail(email)
                    .memberPassword(null)
                    .memberNickname(nickname)
                    .memberKakaoId(kakaoId)
                    .memberKakaoProfileUrl(profileImage)
                    .build();

            memberMapper.insert(newMember);

            return newMember;
        }
    }

    /**
     * 인가 코드를 사용하여 카카오 인증 서버로부터 Access Token을 발급받습니다.
     *
     * @param code 인가 코드
     * @return 발급된 Access Token 문자열
     * @throws BusinessException 토큰 발급 응답이 정상적이지 않을 경우 발생
     */
    private String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        KakaoTokenRequest requestRecord = new KakaoTokenRequest(
                "authorization_code",
                kakaoConfig.getClientId(),
                kakaoConfig.getRedirectUri(),
                code,
                kakaoConfig.getClientSecret()
        );

        HttpEntity<String> request = new HttpEntity<>(requestRecord.toUrlEncodedString(), headers);

        try {
            ResponseEntity<KakaoTokenResponse> response = restTemplate.postForEntity(
                    "https://kauth.kakao.com/oauth/token",
                    request,
                    KakaoTokenResponse.class
            );

            if (response.getBody() == null || response.getBody().getAccessToken() == null) {
                throw new BusinessException(ErrorCode.OAUTH_TOKEN_REQUEST_FAILED);
            }

            return response.getBody().getAccessToken();
        } catch (Exception e) {
            log.error("[OAuth2 Kakao] 카카오 토큰 발급 요청 중 오류가 발생하였습니다. Code: {}", code, e);
            throw new BusinessException(ErrorCode.OAUTH_TOKEN_REQUEST_FAILED);
        }
    }

    /**
     * Access Token을 사용하여 카카오 리소스 서버로부터 사용자 프로필 정보를 조회합니다.
     *
     * @param accessToken 유효한 Access Token 문자열
     * @return 파싱된 사용자 프로필 응답 객체
     * @throws BusinessException 프로필 정보 조회 응답이 정상적이지 않을 경우 발생
     */
    private KakaoUserInfoResponse getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoUserInfoResponse> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    request,
                    KakaoUserInfoResponse.class
            );

            if (response.getBody() == null) {
                throw new BusinessException(ErrorCode.OAUTH_USER_INFO_REQUEST_FAILED);
            }

            return response.getBody();
        } catch (Exception e) {
            log.error("[OAuth2 Kakao] 카카오 사용자 프로필 정보 조회 중 오류가 발생하였습니다.", e);
            throw new BusinessException(ErrorCode.OAUTH_USER_INFO_REQUEST_FAILED);
        }
    }
}