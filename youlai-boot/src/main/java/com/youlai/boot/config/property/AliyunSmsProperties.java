package com.youlai.boot.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 알리윤 SMS 설정
 *
 * @author Ray
 * @since 2024/8/17
 */
@Configuration
@ConfigurationProperties(prefix = "sms.aliyun")
@Data
public class AliyunSmsProperties {

    /**
     * 알리윤 계정의 Access Key ID, API 요청 인증에 사용
     */
    private String accessKeyId;

    /**
     * 알리윤 계정의 Access Key Secret, API 요청 인증에 사용
     */
    private String accessKeySecret;

    /**
     * 알리윤 SMS 서비스 API 도메인 예: dysmsapi.aliyuncs.com
     */
    private String domain;

    /**
     * 알리윤 서비스의 리전 ID, 예: cn-shanghai
     */
    private String regionId;

    /**
     * SMS 서명, 알리윤 SMS 서비스에 등록되고 승인된 것이어야 함
     */
    private String signName;

    /**
     * SMS 템플릿 모음
     */
    private Map<String, String> templates;

}
