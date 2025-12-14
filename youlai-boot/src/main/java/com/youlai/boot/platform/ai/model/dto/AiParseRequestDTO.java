package com.youlai.boot.platform.ai.model.dto;

import lombok.Data;
import java.util.Map;

/**
 * AI 파싱 요청 DTO
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Data
public class AiParseRequestDTO {

    /**
     * 사용자가 입력한 자연어 명령
     */
    private String command;

    /**
     * 현재 페이지 라우트 (컨텍스트용)
     */
    private String currentRoute;

    /**
     * 현재 활성화된 컴포넌트 이름
     */
    private String currentComponent;

    /**
     * 추가 컨텍스트 정보
     */
    private Map<String, Object> context;
}

