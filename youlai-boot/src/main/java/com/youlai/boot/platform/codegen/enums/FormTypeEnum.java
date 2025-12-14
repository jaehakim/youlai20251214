package com.youlai.boot.platform.codegen.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.youlai.boot.common.base.IBaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 폼 유형 열거형
 *
 * @author Ray
 * @since 2.10.0
 */
@Getter
@RequiredArgsConstructor
public enum FormTypeEnum implements IBaseEnum<Integer> {

    /**
     * 입력 상자
     */
    INPUT(1, "입력 상자"),

    /**
     * 드롭다운
     */
    SELECT(2, "드롭다운"),

    /**
     * 라디오 버튼
     */
    RADIO(3, "라디오 버튼"),

    /**
     * 체크박스
     */
    CHECK_BOX(4, "체크박스"),

    /**
     * 숫자 입력 상자
     */
    INPUT_NUMBER(5, "숫자 입력 상자"),

    /**
     * 스위치
     */
    SWITCH(6, "스위치"),

    /**
     * 텍스트 영역
     */
    TEXT_AREA(7, "텍스트 영역"),

    /**
     * 날짜/시간 선택기
     */
    DATE(8, "날짜 선택기"),

    /**
     * 날짜 선택기
     */
    DATE_TIME(9, "날짜/시간 선택기"),

    /**
     * 숨김 필드
     */
    HIDDEN(10, "숨김 필드");


    //  Mybatis-Plus 어노테이션, 데이터베이스에 삽입 시 이 값을 사용
    @EnumValue
    @JsonValue
    private final Integer value;

    // @JsonValue //  열거형 직렬화 시 이 필드를 반환
    private final String label;


    @JsonCreator
    public static FormTypeEnum fromValue(Integer value) {
        for (FormTypeEnum type : FormTypeEnum.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }
}
