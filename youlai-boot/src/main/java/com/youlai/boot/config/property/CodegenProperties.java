package com.youlai.boot.config.property;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.map.MapUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 코드 생성 설정 속성
 *
 * @author Ray
 * @since 2.11.0
 */
@Component
@ConfigurationProperties(prefix = "codegen")
@Data
public class CodegenProperties {


    /**
     * 기본 설정
     */
    private DefaultConfig defaultConfig ;

    /**
     * 템플릿 설정
     */
    private Map<String, TemplateConfig> templateConfigs = MapUtil.newHashMap(true);

    /**
     * 백엔드 애플리케이션 이름
     */
    private String backendAppName;

    /**
     * 프론트엔드 애플리케이션 이름
     */
    private String frontendAppName;

    /**
     * 다운로드 파일명
     */
    private String downloadFileName;

    /**
     * 제외할 데이터 테이블
     */
    private List<String> excludeTables;

    /**
     * 템플릿 설정
     */
    @Data
    public static class TemplateConfig {

        /**
         * 템플릿 경로 (예: /templates/codegen/controller.java.vm)
         */
        private String templatePath;

        /**
         * 서브 패키지명 (예: controller/service/mapper/model)
         */
        private String subpackageName;

        /**
         * 파일 확장자, 예: .java
         */
        private String extension = FileNameUtil.EXT_JAVA;

    }

    /**
     * 기본 설정
     */
    @Data
    public static class DefaultConfig {

        /**
         * 작성자 (예: Ray)
         */
        private String author;

        /**
         * 기본 모듈명 (예: system)
         */
        private String moduleName;

    }


}
