package com.youlai.boot.config;

import cn.hutool.core.util.ArrayUtil;
import com.youlai.boot.config.property.SecurityProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.util.AntPathMatcher;

import java.util.stream.Stream;

/**
 * OpenAPI 인터페이스 문서 설정
 *
 * @author Ray.Hao
 * @see <a href="https://doc.xiaominfo.com/docs/quick-start">knife4j 빠른 시작</a>
 * @since 2023/2/17
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class OpenApiConfig {

    private final Environment environment;

    private final SecurityProperties securityProperties;

    /**
     * 인터페이스 문서 정보
     */
    @Bean
    public OpenAPI openApi() {

        String appVersion = environment.getProperty("project.version", "1.0.0");

        return new OpenAPI()
                .info(new Info()
                        .title("관리 시스템 API 문서")
                        .description("본 문서는 관리 시스템의 모든 API 인터페이스를 포함하며, 로그인 인증, 사용자 관리, 역할 관리, 부서 관리 등의 기능 모듈을 포함하고, 상세한 인터페이스 설명 및 사용 가이드를 제공합니다.")
                        .version(appVersion)
                        .license(new License()
                                .name("Apache License 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0")
                        )
                        .contact(new Contact()
                                .name("youlai")
                                .email("youlaitech@163.com")
                                .url("https://www.youlai.tech")
                        )
                )
                // 전역 인증 매개변수 설정 - Authorize
                .components(new Components()
                        .addSecuritySchemes(HttpHeaders.AUTHORIZATION,
                                new SecurityScheme()
                                        .name(HttpHeaders.AUTHORIZATION)
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .scheme("Bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }


    /**
     * 전역 커스텀 확장
     */
    @Bean
    public GlobalOpenApiCustomizer globalOpenApiCustomizer() {
        return openApi -> {
            // 전역 Authorization 추가
            if (openApi.getPaths() != null) {
                openApi.getPaths().forEach((path, pathItem) -> {

                    // 인증을 무시하는 요청은 Authorization을 포함할 필요 없음
                    String[] ignoreUrls = securityProperties.getIgnoreUrls();
                    if (ArrayUtil.isNotEmpty(ignoreUrls)) {
                        // Ant 매칭으로 무시할 경로를 찾아 Authorization을 추가하지 않음
                        AntPathMatcher antPathMatcher = new AntPathMatcher();
                        if (Stream.of(ignoreUrls).anyMatch(ignoreUrl -> antPathMatcher.match(ignoreUrl, path))) {
                            return;
                        }
                    }

                    // 기타 인터페이스는 통일적으로 Authorization 추가
                    pathItem.readOperations()
                            .forEach(operation ->
                                    operation.addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION))
                            );
                });
            }
        };
    }

}
