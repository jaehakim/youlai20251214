package com.youlai.boot.common.enums;

import com.youlai.boot.common.base.IBaseEnum;
import lombok.Getter;

/**
 * 상태 열거형
 *
 * @author haoxr
 * @since 2022/10/14
 */
@Getter
public enum StatusEnum implements IBaseEnum<Integer> {

    ENABLE(1, "활성화"),
    DISABLE (0, "비활성화");

    private final Integer value;


    private final String label;

    StatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
