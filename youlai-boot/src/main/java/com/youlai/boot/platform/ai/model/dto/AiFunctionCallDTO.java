package com.youlai.boot.platform.ai.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

/**
 * AI 함수 호출 DTO
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiFunctionCallDTO {

    /**
     * 함수 이름
     */
    private String name;

    /**
     * 함수 설명
     */
    private String description;

    /**
     * 파라미터 객체
     */
    private Map<String, Object> arguments;
}

