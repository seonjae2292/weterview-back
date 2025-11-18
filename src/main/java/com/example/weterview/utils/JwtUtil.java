package com.example.weterview.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Collections;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
    private static final String KAKAO_USER_NUMBER = "kakaoUserNumber";
    private static final String NAME = "name";


    @PostConstruct
    protected void init() {
        // String 형태의 secretKey를 SecretKey 객체로 변환
        // HS256 알고리즘을 사용하므로, 키의 길이는 최소 256비트 (32바이트) 이상
        byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * HTTP 요청 헤더에서 "Bearer" 토큰을 추출합니다.
     * Authorization 헤더가 존재하고 "Bearer "로 시작하는 경우, 접두사를 제거한 실제 토큰을 반환합니다.
     *
     * @param request HTTP 요청 객체
     * @return 추출된 JWT 문자열, 토큰이 없거나 형식이 올바르지 않으면 null
     */
    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    /**
     * 주어진 사용자 정보로 Access Token을 생성합니다.
     * 토큰에는 카카오 사용자 번호, 이름, 토큰 타입("access") 클레임과 만료 시간이 포함됩니다.
     *
     * @param kakaoUserNumber 카카오 사용자 식별 번호
     * @param name            사용자 이름
     * @return 생성된 JWT 문자열 (Access Token)
     */
    public String generateAccessToken(String kakaoUserNumber, String name) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(KAKAO_USER_NUMBER, kakaoUserNumber);
        claims.put(NAME, name);
        claims.put(KEY_TOKEN_TYPE, TOKEN_TYPE_ACCESS);

        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenExpirationMillis);

        return Jwts.builder()
                .subject(kakaoUserNumber) // sub claim으로 kakaoUserNumber 추가
                .claims(claims)
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 주어진 사용자 이름으로 Refresh Token을 생성합니다.
     * 토큰에는 주체(sub)로 사용자 이름, 토큰 타입("refresh") 클레임과 만료 시간이 포함됩니다.
     *
     * @param username 사용자 이름 (리프레시 토큰의 주체)
     * @return 생성된 JWT 문자열 (Refresh Token)
     */
//    public String generateRefreshToken(String username) {
//        Claims claims = Jwts.claims().subject(username).build();
//        claims.put(KEY_TOKEN_TYPE, TOKEN_TYPE_REFRESH);
//
//        Date now = new Date();
//        Date validity = new Date(now.getTime() + refreshTokenExpirationMillis);
//
//        return Jwts.builder()
//                .claims(claims)
//                .issuedAt(now)
//                .expiration(validity)
//                .signWith(secretKey, Jwts.SIG.HS256)
//                .compact();
//    }

    /**
     * 주어진 토큰에서 모든 클레임(payload)을 추출합니다.
     * 토큰의 서명을 검증하고, 유효한 경우 페이로드를 반환합니다.
     *
     * @param token 파싱할 JWT 문자열
     * @return 토큰의 페이로드(Claims) 객체
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 주어진 토큰에서 카카오 사용자 식별 번호를 추출합니다.
     * JWT의 'sub'(주체) 클레임에 해당하는 값을 반환합니다.
     *
     * @param token JWT 문자열 (Bearer 접두사 포함 가능)
     * @return 카카오 사용자 식별 번호 (String)
     */
    public String extractKakaoUserNumber(String token) {
        String jwt = token.replace("Bearer ", "");
        return getAllClaimsFromToken(jwt).getSubject();
    }

    /**
     * 주어진 토큰에서 "사용자의 카카오 회원번호"를 추출
     * JWT의 "name" 클레임에 해당하는 값을 반환합니다.
     *
     * @param token JWT 문자열
     * @return 사용자의 카카오 회원번호
     */
    public String getKakaoUserNumFromToken(String token) {
        String jwt = token.replace("Bearer ", "");
        try {
//            return Jwts
//                    .parser()
//                    .verifyWith(secretKey)
//                    .build()
//                    .parseSignedClaims(jwt)
//                    .getPayload().get(NAME, String.class);

            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();

            log.info("JWT claims = {}", claims); // 여기에 실제 키들이 다 찍힘

            return claims.get("kakaoUserNumber", String.class);
        } catch (Exception e) {
            log.info("사용자 이름 추출 에러" + e.toString());
            return "사용자 이름 추출 에러";
        }
    }

    /**
     * 토큰의 만료 여부를 확인합니다.
     * 토큰의 만료 시간이 현재 시간보다 이전인지 검사합니다.
     *
     * @param token 확인할 JWT 문자열
     * @return 토큰이 만료되지 않았으면 true, 만료되었으면 false
     */
    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return !expiration.before(new Date());
    }

    // Authentication 객체 생성
    /**
     * 토큰을 기반으로 Spring Security의 Authentication 객체를 생성합니다.
     * Principal(주체)에 사용자 식별 번호를 담고, 자격 증명은 null로 처리합니다.
     * 권한(Authorities) 정보는 현재는 비어있는 리스트를 사용합니다.
     *
     * @param token JWT 문자열
     * @return 인증된 Authentication 객체
     */
    public Authentication createAuthentication(String token) {
        String kakaoUserNumber = extractKakaoUserNumber(token);
        // 사용자 외 단일 user만 존재할 때는 principal에 username만 사용, 권한 필요시 빈 리스트
        return new UsernamePasswordAuthenticationToken(
                kakaoUserNumber, null, Collections.emptyList()
        );
    }

    // 토큰 만료일 추출
    /**
     * 주어진 토큰에서 만료일(Expiration Date)을 추출합니다.
     *
     * @param token JWT 문자열
     * @return 만료일(Date) 객체
     */
    private Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }
}
