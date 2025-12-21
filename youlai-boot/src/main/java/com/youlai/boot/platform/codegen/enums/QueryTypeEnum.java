package com.youlai.boot.platform.codegen.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.youlai.boot.common.base.IBaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 쿼리 유형 열거형
 *
 * @author Ray
 * @since 2.10.0
 */
@Getter
@RequiredArgsConstructor
public enum QueryTypeEnum implements IBaseEnum<Integer> {

    /** 같음 */
    EQ(1, "="),

    /** 모호 매칭 */
    LIKE(2, "LIKE '%s%'"),

    /** 포함 */
    IN(3, "IN"),

    /** 범위 */
    BETWEEN(4, "BETWEEN"),

    /** 크다 */
    GT(5, ">"),

    /** 크다같음 */
    GE(6, ">="),

    /** 작다 */
    LT(7, "<"),

    /** 작다같음 */
    LE(8, "<="),

    /** 같지 않음 */
    NE(9, "!="),

    /** 왼쪽 모호 매칭 */
    LIKE_LEFT(10, "LIKE '%s'"),

    /** 오른쪽 모호 매칭 */
    LIKE_RIGHT(11, "LIKE 's%'");


    // 데이터베이스에 저장되는 열거형 속성 값
    @EnumValue
    @JsonValue
    private final Integer value;

    // JSON으로 직렬화 시 속성 값
    private final String label;


    @JsonCreator
    public static QueryTypeEnum fromValue(Integer value) {
        for (QueryTypeEnum type : QueryTypeEnum.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }

}
