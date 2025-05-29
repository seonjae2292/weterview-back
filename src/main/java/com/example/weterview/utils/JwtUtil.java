package com.example.weterview.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {
    @Value("${jwt.secret-key}")
    private String secretKeyString;
    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpirationMillis;
    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpirationMillis;

    private SecretKey secretKey;

    private static final String KEY_ROLES = "roles";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";
    private static final String KEY_TOKEN_TYPE = "type";

    @PostConstruct
    protected void init() {
        // String 형태의 secretKey를 SecretKey 객체로 변환
        // HS256 알고리즘을 사용하므로, 키의 길이는 최소 256비트 (32바이트) 이상을 권장합니다.
        byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // 엑세스 토큰 생성 String
    public String generateAccessToken(String username) {
        Claims claims = Jwts.claims().subject(username).build();
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
        return getAllClaimsFromToken(token).getSubject();
    }

    // 토큰 만료일 추출
    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    // 토큰 만료 여부 확인 boolean
    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String getTokenType(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get(KEY_TOKEN_TYPE, String.class);
    }

    public boolean isAccessToken(String token) {
        String tokenType = getTokenType(token);
        return TOKEN_TYPE_ACCESS.equals(tokenType);
    }

    public boolean isRefreshToken(String token) {
        String tokenType = getTokenType(token);
        return TOKEN_TYPE_REFRESH.equals(tokenType);
    }
}
