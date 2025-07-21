package com.example.weterview.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Collections;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component

public class JwtUtil {
    @Value("${jwt.secret-key}")
    private String secretKeyString;
    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpirationMillis;
    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpirationMillis;

    private SecretKey secretKey;

//    private static final String KEY_ROLES = "roles"; TODO 뭔지 알아보기
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";
    private static final String KEY_TOKEN_TYPE = "type";

    @PostConstruct
    protected void init() {
        // String 형태의 secretKey를 SecretKey 객체로 변환
        // HS256 알고리즘을 사용하므로, 키의 길이는 최소 256비트 (32바이트) 이상
        byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // request header에서 토큰 추출하기
    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    // 엑세스 토큰 생성 String
    public String generateAccessToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(Claims.SUBJECT, username);
        claims.put(KEY_TOKEN_TYPE, TOKEN_TYPE_ACCESS);

        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenExpirationMillis);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    // 리프레쉬 토큰 생성 String
    public String generateRefreshToken(String username) {
        Claims claims = Jwts.claims().subject(username).build();
        claims.put(KEY_TOKEN_TYPE, TOKEN_TYPE_REFRESH);

        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenExpirationMillis);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    // 토큰에서 모든 Claim 추출
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 토큰에서 사용자 이름 추출 String
    public String getUsernameFromToken(String token) {
        String jwt = token.replace("Bearer ", "");
        return getAllClaimsFromToken(jwt).getSubject();
    }

    // 토큰 만료 시간에 따른 만료 여부 확인
    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return !expiration.before(new Date());
    }

    // Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        String username = getUsernameFromToken(token);
        // 사용자 외 단일 user만 존재할 때는 principal에 username만 사용, 권한 필요시 빈 리스트
        return new UsernamePasswordAuthenticationToken(
                username,
                token,
                Collections.emptyList()
        );
    }

    // 토큰 만료일 추출
    private Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }
}
