package com.youlai.boot.common.annotation;


import java.lang.annotation.*;

/**
 * 중복 제출 방지 어노테이션
 * <p>
 * 이 어노테이션은 메서드에 사용되며, 지정된 시간 내에 중복 제출을 방지합니다. 기본 시간은 5초입니다.
 *
 * @author Ray.Hao
 * @since 2.3.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RepeatSubmit {

    /**
     * 락 만료 시간 (초)
     * <p>
     * 기본 5초 이내 중복 제출 불가
     */
    int expire() default 5;

}
