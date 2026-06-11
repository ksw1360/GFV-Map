package com.tj.GFV_Map.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "https://master.d2ahqjf7y3gh6b.amplifyapp.com", // Amplify (슬래시 X)
                        "https://hjp7208.site",                         // 가비아
                        "http://hjp7208.site",
                        "http://localhost:3000",                        // 로컬 개발
                        "http://192.168.7.120:3000"                     // LAN 테스트 (경로 X)
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}