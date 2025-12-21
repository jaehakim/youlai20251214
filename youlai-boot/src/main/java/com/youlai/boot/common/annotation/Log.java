package com.youlai.boot.common.annotation;

import com.youlai.boot.common.enums.LogModuleEnum;

import java.lang.annotation.*;

/**
 * 로그 어노테이션
 *
 * @author Ray
 * @since 2024/6/25
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Log {

    /**
     * 로그 설명
     *
     * @return 로그 설명
     */
    String value() default "";

    /**
     * 로그 모듈
     *
     * @return 로그 모듈
     */

    LogModuleEnum module();

    /**
     * 요청 파라미터 기록 여부
     *
     * @return 요청 파라미터 기록 여부
     */
    boolean params() default true;

    /**
     * 응답 결과 기록 여부
     * <br/>
     * 응답 결과는 기본적으로 기록하지 않음 (로그 크기 방지)
     * @return 응답 결과 기록 여부
     */
    boolean result() default false;


}