package com.youlai.boot.core.validator;

import com.youlai.boot.common.annotation.ValidField;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

/**
 * 필드 검증기
 *
 * @author Ray.Hao
 * @since 2024/11/18
 */
public class FieldValidator implements ConstraintValidator<ValidField, String> {

    private String[] allowedValues;

    @Override
    public void initialize(ValidField constraintAnnotation) {
        // 허용된 값 목록 초기화
        this.allowedValues = constraintAnnotation.allowedValues();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // 필드가 null을 허용하는 경우 true 반환
        }
        // 값이 허용 목록에 있는지 확인
        return Arrays.asList(allowedValues).contains(value);
    }
}
