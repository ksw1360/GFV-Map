package com.tj.GFV_Map.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())              // 개발 중엔 CSRF off
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()              // 모든 요청 허용
                )
                .formLogin(form -> form.disable())         // 기본 로그인 화면 끄기
                .httpBasic(basic -> basic.disable());      // Basic 인증도 끄기

        return http.build();
    }
}