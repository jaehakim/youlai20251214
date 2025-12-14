package com.youlai.boot.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 로그 모듈 열거형
 *
 * @author Ray
 * @since 2.10.0
 */
@Schema(enumAsRef = true)
@Getter
public enum LogModuleEnum {

    EXCEPTION("예외"),
    LOGIN("로그인"),
    USER("사용자"),
    DEPT("부서"),
    ROLE("역할"),
    MENU("메뉴"),
    DICT("사전"),
    SETTING("시스템 설정"),
    OTHER("기타");

    @JsonValue
    private final String moduleName;

    LogModuleEnum(String moduleName) {
        this.moduleName = moduleName;
    }
}