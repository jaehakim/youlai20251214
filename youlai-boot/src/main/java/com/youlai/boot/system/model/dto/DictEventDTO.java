package com.youlai.boot.system.model.dto;

import lombok.Data;

/**
 * 사전 업데이트 이벤트 메시지
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Data
public class DictEventDTO {
    /**
     * 사전 코드
     */
    private String dictCode;

    /**
     * 타임스탬프
     */
    private long timestamp;

    public DictEventDTO(String dictCode) {
        this.dictCode = dictCode;
        this.timestamp = System.currentTimeMillis();
    }
}

