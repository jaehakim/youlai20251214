package com.youlai.boot.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;

/**
 * CORS 리소스 공유 설정
 *
 * @author haoxr
 * @since 2023/4/17
 */
@Configuration
public class CorsConfig {

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //1.모든 출처 허용
        corsConfiguration.setAllowedOriginPatterns(Collections.singletonList("*"));
        //2.모든 요청 헤더 허용
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        //3.모든 메서드 허용
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
        //4.자격 증명 허용
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        CorsFilter corsFilter = new CorsFilter(source);

        FilterRegistrationBean<CorsFilter> filterRegistrationBean=new FilterRegistrationBean<>(corsFilter);
        filterRegistrationBean.setOrder(-101);  // SpringSecurity Filter의 Order(-100)보다 작으면 됨

        return filterRegistrationBean;
    }
}