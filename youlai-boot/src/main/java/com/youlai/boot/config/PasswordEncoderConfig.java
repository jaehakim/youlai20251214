package com.youlai.boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 비밀번호 인코더
 *
 * @author Ray.Hao
 * @since 2024/12/3
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * 비밀번호 인코더
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
