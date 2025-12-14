package com.youlai.boot.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RequestMethodEnum {
    /**
     * @AnonymousGetMapping 검색
     */
    GET("GET"),

    /**
     * @AnonymousPostMapping 검색
     */
    POST("POST"),

    /**
     * @AnonymousPutMapping 검색
     */
    PUT("PUT"),

    /**
     * @AnonymousPatchMapping 검색
     */
    PATCH("PATCH"),

    /**
     * @AnonymousDeleteMapping 검색
     */
    DELETE("DELETE"),

    /**
     * 그 외에는 모든 Request 인터페이스를 허용
     */
    ALL("All");

    /**
     * Request 유형
     */
    private final String type;

    public static RequestMethodEnum find(String type) {
        for (RequestMethodEnum value : RequestMethodEnum.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return ALL;
    }
}
