package com.youlai.boot.system.enums;

import com.youlai.boot.common.base.IBaseEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 공지 발행 상태 열거형
 *
 * @author Ray.Hao
 * @since 2024/10/14
 */
@Getter
@Schema(enumAsRef = true)
public enum NoticePublishStatusEnum implements IBaseEnum<Integer> {

    UNPUBLISHED(0, "미발행"),
    PUBLISHED(1, "발행됨"),
    REVOKED(-1, "철회됨");


    private final Integer value;

    private final String label;

    NoticePublishStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
