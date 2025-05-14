package com.example.weterview.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.kakao")
@Getter
@Setter
public class KakaoOAuthConfig {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
}
