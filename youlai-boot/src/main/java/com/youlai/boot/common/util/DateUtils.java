
package com.youlai.boot.common.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.format.annotation.DateTimeFormat;

import java.lang.reflect.Field;

/**
 * 날짜 유틸리티 클래스
 *
 * @author haoxr
 * @since 2.4.2
 */
public class DateUtils {

    /**
     * 기간 날짜를 데이터베이스 날짜 형식으로 포맷
     * <p>
     * 예시: 2021-01-01 → 2021-01-01 00:00:00
     *
     * @param obj                처리할 객체
     * @param startTimeFieldName 시작 시간 필드명
     * @param endTimeFieldName   종료 시간 필드명
     */
    public static void toDatabaseFormat(Object obj, String startTimeFieldName, String endTimeFieldName) {
        Field startTimeField = ReflectUtil.getField(obj.getClass(), startTimeFieldName);
        Field endTimeField = ReflectUtil.getField(obj.getClass(), endTimeFieldName);

        if (startTimeField != null) {
            processDateTimeField(obj, startTimeField, startTimeFieldName, "yyyy-MM-dd 00:00:00");
        }

        if (endTimeField != null) {
            processDateTimeField(obj, endTimeField, endTimeFieldName, "yyyy-MM-dd 23:59:59");
        }
    }

    /**
     * 날짜 필드 처리
     *
     * @param obj           처리할 객체
     * @param field         필드
     * @param fieldName     필드명
     * @param targetPattern 목표 데이터베이스 날짜 형식
     */
    private static void processDateTimeField(Object obj, Field field, String fieldName, String targetPattern) {
        Object fieldValue = ReflectUtil.getFieldValue(obj, fieldName);
        if (fieldValue != null) {
            // 원본 날짜 형식 가져오기
            String pattern = field.isAnnotationPresent(DateTimeFormat.class) ? field.getAnnotation(DateTimeFormat.class).pattern() : "yyyy-MM-dd";
            // 날짜 객체로 변환
            DateTime dateTime = DateUtil.parse(StrUtil.toString(fieldValue), pattern);
            // 목표 데이터베이스 날짜 형식으로 변환
            ReflectUtil.setFieldValue(obj, fieldName, dateTime.toString(targetPattern));
        }
    }
}
