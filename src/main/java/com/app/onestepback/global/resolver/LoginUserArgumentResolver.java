package com.app.onestepback.global.resolver;

import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.global.annotation.LoginUser;
import com.app.onestepback.global.exception.LoginRequiredException;
import com.app.onestepback.global.exception.NotArtistException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 컨트롤러 메서드의 파라미터로 전달된 {@link LoginUser} 어노테이션을 해석하여
 * 현재 인증된 사용자(SessionUser) 객체를 동적으로 주입하는 아규먼트 리졸버입니다.
 * <p>
 * HTTP 세션 기반의 인증 상태를 확인하며, 어노테이션의 속성에 따라
 * 필수 로그인 여부 및 아티스트 전용 권한 검증을 함께 수행합니다.
 * </p>
 */
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 리졸버가 해당 파라미터를 지원하는지 여부를 검사합니다.
     *
     * @param parameter 컨트롤러 메서드의 파라미터 정보
     * @return {@link LoginUser} 어노테이션이 존재하고, 파라미터 타입이 {@link SessionUser}인 경우 true 반환
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isLoginUserAnnotation = parameter.getParameterAnnotation(LoginUser.class) != null;
        boolean isUserClass = SessionUser.class.equals(parameter.getParameterType());
        return isLoginUserAnnotation && isUserClass;
    }

    /**
     * 파라미터에 주입할 실제 객체를 해석하고 반환합니다.
     * <p>
     * HTTP 요청으로부터 세션을 추출하여 사용자 정보를 확인하며,
     * 인가되지 않은 접근 시 설정된 정책(required, artistOnly)에 따라 적절한 예외를 발생시킵니다.
     * </p>
     *
     * @param parameter     메서드 파라미터 정보
     * @param mavContainer  현재 요청에 대한 모델 및 뷰 컨테이너
     * @param webRequest    현재 진행 중인 웹 요청 정보
     * @param binderFactory 데이터 바인딩 팩토리
     * @return 요청한 세션 사용자 객체 (인증되지 않았으나 필수가 아닌 경우 null 반환)
     * @throws LoginRequiredException 로그인이 필수인 자원에 비인증 사용자가 접근할 경우 발생
     * @throws NotArtistException     아티스트 전용 자원에 일반 사용자가 접근할 경우 발생
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        HttpSession session = request.getSession(false);
        SessionUser sessionUser = (session != null) ? (SessionUser) session.getAttribute("member") : null;

        LoginUser loginUser = parameter.getParameterAnnotation(LoginUser.class);
        assert loginUser != null;

        if (sessionUser == null) {
            if (loginUser.required()) {
                throw new LoginRequiredException("로그인이 필요한 서비스입니다.");
            }
            return null;
        }

        if (loginUser.artistOnly() && !sessionUser.isArtist()) {
            throw new NotArtistException("아티스트 권한이 필요한 서비스입니다.");
        }

        return sessionUser;
    }
}