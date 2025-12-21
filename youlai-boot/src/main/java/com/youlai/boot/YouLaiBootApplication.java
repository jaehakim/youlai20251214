package com.youlai.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * 애플리케이션 시작 클래스
 *
 * @author Ray.Hao
 * @since 0.0.1
 */
@SpringBootApplication
public class YouLaiBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(YouLaiBootApplication.class, args);
    }

}
