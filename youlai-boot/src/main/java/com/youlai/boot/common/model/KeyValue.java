package com.youlai.boot.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 키-값 쌍
 *
 * @author haoxr
 * @since 2024/5/25
 */
@Schema(description = "키-값 쌍")
@Data
@NoArgsConstructor
public class KeyValue {

    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Schema(description = "옵션 값")
    private String key;

    @Schema(description = "옵션 라벨")
    private String value;

}