package com.youlai.boot.system.enums;

import com.youlai.boot.common.base.IBaseEnum;
import lombok.Getter;

/**
 * 사전 코드 열거형
 *
 * @author Ray.Hao
 * @since 2024/10/30
 */
@Getter
public enum DictCodeEnum implements IBaseEnum<String> {

    GENDER("gender", "성별"),
    NOTICE_TYPE("notice_type", "알림 유형"),
    NOTICE_LEVEL("notice_level", "알림 레벨");

    private final String value;

    private final String label;

    DictCodeEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

}
