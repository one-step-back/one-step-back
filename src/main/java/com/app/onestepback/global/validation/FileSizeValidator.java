package com.app.onestepback.global.validation;

import com.app.onestepback.global.annotation.MaxFileSize;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

/**
 * 업로드된 파일의 크기가 지정된 최대 허용치를 초과하는지 검증하는 Validator 클래스입니다.
 * <p>
 * {@link MaxFileSize} 어노테이션과 함께 동작하며, 입력된 메가바이트(MB) 단위의 설정값을
 * 바이트(Byte) 단위로 변환하여 실제 파일 크기와 비교합니다.
 * 파일이 첨부되지 않은 경우(null 또는 empty) 필수 검증 대상이 아니라고 판단하여 기본적으로 검증을 통과시킵니다.
 * </p>
 */
public class FileSizeValidator implements ConstraintValidator<MaxFileSize, MultipartFile> {

    private long maxSize;

    /**
     * 제약 조건 어노테이션의 설정값을 기반으로 검증기를 초기화합니다.
     *
     * @param constraintAnnotation 대상 필드에 선언된 MaxFileSize 어노테이션 인스턴스
     */
    @Override
    public void initialize(MaxFileSize constraintAnnotation) {
        this.maxSize = constraintAnnotation.max() * 1024 * 1024;
    }

    /**
     * 클라이언트로부터 전달받은 MultipartFile 객체의 크기가 허용된 최대치 이하인지 확인합니다.
     *
     * @param file    유효성을 검증할 업로드 파일 객체
     * @param context 제약 조건 검증 컨텍스트
     * @return 파일이 존재하지 않거나 허용치 이하일 경우 true, 제한을 초과할 경우 false 반환
     */
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true;
        }
        return file.getSize() <= maxSize;
    }
}