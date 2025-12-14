package com.youlai.boot.system.enums;

import com.youlai.boot.common.base.IBaseEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 알림 대상 유형 열거형
 *
 * @author Ray.Hao
 * @since 2024/10/14
 */
@Getter
@Schema(enumAsRef = true)
public enum NoticeTargetEnum implements IBaseEnum<Integer> {

    ALL(1, "전체"),
    SPECIFIED(2, "지정됨");


    private final Integer value;

    private final String label;

    NoticeTargetEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
