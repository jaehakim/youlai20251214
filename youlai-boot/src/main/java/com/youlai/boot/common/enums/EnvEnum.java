package com.youlai.boot.common.enums;

import com.youlai.boot.common.base.IBaseEnum;
import lombok.Getter;

/**
 * 환경 열거형
 *
 * @author Ray
 * @since 4.0.0
 */
@Getter
public enum EnvEnum implements IBaseEnum<String> {

    DEV("dev", "개발 환경"),
    PROD("prod", "프로덕션 환경");

    private final String value;

    private final String label;

    EnvEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }
}
