package com.example.weterview.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:8080", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                // 클라이언트가 응답 헤더 중 Authorization 헤더를 읽을 수 있도록 허용
                // JWT 토큰을 Authorization 헤더에 담아 전달하는 경우 필요
                .exposedHeaders("Authorization")  // JWT 토큰을 사용할 경우 필요
                // 자격 증명(쿠키, 인증 헤더) 전송 허용 여부
                .allowCredentials(true)
                .maxAge(3600);
    }
}
