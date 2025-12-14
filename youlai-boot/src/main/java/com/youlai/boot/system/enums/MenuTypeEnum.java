package com.youlai.boot.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.youlai.boot.common.base.IBaseEnum;
import lombok.Getter;

/**
 * 메뉴 유형 열거형
 *
 * @author Ray.Hao
 * @since 2022/4/23 9:36
 */
@Getter
public enum MenuTypeEnum implements IBaseEnum<Integer> {

    NULL(0, null),
    MENU(1, "메뉴"),
    CATALOG(2, "디렉토리"),
    EXTLINK(3, "외부 링크"),
    BUTTON(4, "버튼");

    //  Mybatis-Plus 어노테이션, 데이터베이스에 삽입 시 이 값을 사용
    @EnumValue
    private final Integer value;

    // @JsonValue //  열거형 직렬화 시 이 필드를 반환
    private final String label;

    MenuTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

}
