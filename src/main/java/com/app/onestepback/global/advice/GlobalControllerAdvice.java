package com.app.onestepback.global.advice;

import com.app.onestepback.global.config.PortOneConfig;
import com.app.onestepback.global.exception.LoginRequiredException;
import com.app.onestepback.global.exception.NotArtistException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 웹 뷰(View) 렌더링을 담당하는 컨트롤러에서 발생하는 예외를 전역으로 가로채어 처리하는 어드바이스 클래스입니다.
 */
@Slf4j
@ControllerAdvice(basePackages = "com.app.onestepback.web.controller")
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final PortOneConfig portOneConfig;

    /**
     * 뷰 렌더링 시 포트원(PortOne) 가맹점 식별 코드를 전역 모델 속성으로 추가합니다.
     *
     * @return 포트원 가맹점 식별 코드(Merchant ID)
     */
    @ModelAttribute("portoneMerchantId")
    public String addPortOneMerchantId() {
        return portOneConfig.getMerchantId();
    }

    /**
     * 인증되지 않은 사용자가 보호된 자원에 접근할 때 발생하는 예외를 처리합니다.
     * 사용자를 로그인 페이지로 리다이렉트합니다.
     *
     * @return 로그인 페이지 리다이렉트 URI 경로
     */
    @ExceptionHandler(LoginRequiredException.class)
    public String handleLoginRequiredException() {
        return "redirect:/member/login?need-login=true";
    }

    /**
     * 아티스트 권한이 없는 사용자가 아티스트 전용 자원에 접근할 때 발생하는 예외를 처리합니다.
     *
     * @return 403 접근 권한 없음 오류 페이지
     */
    @ExceptionHandler(NotArtistException.class)
    public String handleNotArtistException() {
        return "error/403";
    }

    /**
     * 클라이언트가 요청한 리소스를 찾을 수 없을 때 발생하는 예외를 처리합니다.
     *
     * @return 404 리소스 없음 오류 페이지
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFoundException() {
        return "error/404";
    }

    /**
     * 명시적으로 처리되지 않은 모든 서버 내부 예외를 처리합니다.
     *
     * @param e 발생한 예외 객체
     * @return 500 서버 내부 오류 페이지
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e) {
        log.error("[View Error] ", e);
        return "error/500";
    }
}