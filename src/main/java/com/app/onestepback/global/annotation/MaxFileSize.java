package com.app.onestepback.global.annotation;

import com.app.onestepback.global.validation.FileSizeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * 업로드된 파일의 최대 크기를 검증하기 위한 커스텀 제약 조건(Constraint) 어노테이션입니다.
 * <p>
 * {@link FileSizeValidator} 클래스를 통해 실제 유효성 검증 로직이 수행되며,
 * 명시된 크기(MB 단위)를 초과하는 파일이 입력될 경우 바인딩 오류를 발생시킵니다.
 * </p>
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileSizeValidator.class)
public @interface MaxFileSize {

    /**
     * 유효성 검증 실패 시 클라이언트에게 반환될 기본 오류 메시지입니다.
     *
     * @return 검증 실패 메시지 문자열
     */
    String message() default "파일 크기가 허용된 한도를 초과했습니다.";

    /**
     * 허용되는 최대 파일 크기를 메가바이트(MB) 단위로 설정합니다.
     *
     * @return 허용 최대 크기 (기본값: 5MB)
     */
    long max() default 5;

    /**
     * 특정 검증 그룹을 지정하여 상황에 따라 유효성 검사를 분리할 때 사용합니다.
     *
     * @return 지정된 유효성 검증 그룹의 클래스 배열
     */
    Class<?>[] groups() default {};

    /**
     * 검증 클라이언트에 의해 사용될 수 있는 추가적인 메타데이터 페이로드를 지정합니다.
     *
     * @return 지정된 페이로드 클래스 배열
     */
    Class<? extends Payload>[] payload() default {};
}