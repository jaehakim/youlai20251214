package com.youlai.boot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;

/**
 * Web 설정
 *
 * @author Ray.Hao
 * @since 2020/10/16
 */
@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Value("${oss.local.storage-path}")
    private String ossLocalStoragePath;

    /**
     * 메시지 변환기 설정
     *
     * @param converters 메시지 변환기 목록
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();

        // JavaTimeModule 등록 (수동 LocalDateTimeSerializer 등록 대체)
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        // 지정된 문자열 형식으로 반환
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME_FORMATTER));
        // 역직렬화, 프론트엔드에서 전달된 형식 수용
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DATE_TIME_FORMATTER));
        objectMapper.registerModule(javaTimeModule);

        // 전역 날짜 형식 및 시간대 설정
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        // Long/BigInteger 정밀도 문제 처리
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);

        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
        converters.add(1, jackson2HttpMessageConverter);
    }

    /**
     * 로컬 파일 저장소 정적 리소스 서빙 설정
     *
     * @param registry 리소스 핸들러 레지스트리
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Windows와 Unix 경로 형식 모두 지원
        // File 객체를 사용하여 OS에 맞는 URI 생성
        File uploadDir = new File(ossLocalStoragePath);
        String resourceLocation = uploadDir.toURI().toString();

        log.info("파일 업로드 리소스 위치 설정: {} → {}", ossLocalStoragePath, resourceLocation);

        registry.addResourceHandler("/upload/**")
                .addResourceLocations(resourceLocation);
    }

    /**
     * 검증기 설정
     *
     * @param autowireCapableBeanFactory SpringConstraintValidatorFactory 주입에 사용
     * @return Validator 인스턴스
     */
    @Bean
    public Validator validator(final AutowireCapableBeanFactory autowireCapableBeanFactory) {
        try (ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .failFast(true) // failFast=true일 때, 첫 번째 검증 실패 시 즉시 반환, false는 모든 파라미터 검증
                .constraintValidatorFactory(new SpringConstraintValidatorFactory(autowireCapableBeanFactory))
                .buildValidatorFactory()) {

            // try-with-resources 사용으로 ValidatorFactory가 올바르게 닫히도록 보장
            return validatorFactory.getValidator();
        }
    }
}
