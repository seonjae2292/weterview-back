package com.example.weterview.config;

import com.example.weterview.repository.UserRepository;
import com.example.weterview.utils.JwtAuthenticationFilter;
import com.example.weterview.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private static final String[] OAUTH_URL = {
            "/oauth/**",
            "/auth/**",
            "/studygroup/get/**",
    };

    private static final String[] SWAGGER_URL = {
            "/v3/api-docs/**", // Swagger UI 리소스
            "/swagger-ui/**", // Swagger UI 페이지
            "/swagger-ui.html" // OpenAPI 명세서
    };

    /**
     * JWT 인증 필터 빈 등록
     * - 들어오는 요청의 헤더에서 토큰을 꺼내서 검증하고,
     *   유효하면 SecurityContext에 인증 정보 세팅.
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(userRepository, jwtUtil);
    }

    /**
     * 보안 필터 체인 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configure(http))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(OAUTH_URL).permitAll()
                                .requestMatchers(SWAGGER_URL).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * 비밀번호 암호화에 사용할 BCrypt 인코더 빈 등록
     * - 회원가입 등에서 비밀번호를 해시할 때 사용
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 프론트엔드 URL 허용
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8080"));

        // 모든 메서드 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // 모든 헤더 허용
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // 쿠키/인증정보 허용
        configuration.setAllowCredentials(true);

        // Authorization 헤더 노출 (JWT 사용 시 필수)
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}