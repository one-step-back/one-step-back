package com.app.onestepback.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 현재 인증된 사용자(세션) 정보를 컨트롤러의 파라미터로 주입하기 위한 커스텀 어노테이션입니다.
 * <p>
 * 리졸버(ArgumentResolver)와 연계되어 동작하며,
 * 어노테이션 속성 값을 통해 로그인 필수 여부 및 아티스트 전용 자원 접근 권한을 선언적으로 제어할 수 있습니다.
 * </p>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUser {

    /**
     * 해당 자원 접근 시 인증(로그인) 필수 여부를 설정합니다.
     * <p>
     * true일 경우 미인증 상태에서의 접근을 차단하고 예외를 발생시키며,
     * false일 경우 예외를 발생시키지 않고 null을 주입합니다.
     * </p>
     *
     * @return 인증 필수 여부 (기본값: true)
     */
    boolean required() default true;

    /**
     * 해당 자원 접근 시 아티스트 권한 필수 여부를 설정합니다.
     * <p>
     * true일 경우 일반 회원 권한으로 접근 시 인가 예외를 발생시킵니다.
     * </p>
     *
     * @return 아티스트 권한 필수 여부 (기본값: false)
     */
    boolean artistOnly() default false;
}