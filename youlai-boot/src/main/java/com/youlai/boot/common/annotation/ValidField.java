package com.youlai.boot.common.annotation;

import com.youlai.boot.core.validator.FieldValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 필드 값의 유효성을 검증하기 위한 어노테이션
 *
 * @author Ray.Hao
 * @since 2.18.0
 */
@Documented
@Constraint(validatedBy = FieldValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidField {

    /**
     * 검증 실패 시 오류 메시지
     */
    String message() default "비정상 필드";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 허용되는 유효한 값 목록
     */
    String[] allowedValues();

}
