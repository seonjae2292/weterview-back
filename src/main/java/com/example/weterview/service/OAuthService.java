package com.example.weterview.service;

import com.example.weterview.dto.KakaoIdTokenInfoRes;
import com.example.weterview.dto.KakaoTokenRes;
import com.example.weterview.entity.User;
import com.example.weterview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {
    private final WebClient webClient;
    private final UserRepository userRepository;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String tokenUri;

    @Value("${kakao.token-info-url}")
    private String kakaoInfoUrl;

    public Mono<String> postVerifyUserToKakao(String code) {
        return getKakaoToken(code)
                .flatMap(this::getKakaoUserInfo)
                .flatMap(this::checkUserExistence)
                .doOnError(error -> log.error("OAuth verification failed: ", error));
    }

    private Mono<KakaoTokenRes> getKakaoToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("client_secret", clientSecret);
        params.add("grant_type", "authorization_code");

        return webClient.post()
                .uri(tokenUri)
                .body(BodyInserters.fromFormData(params))
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("Kakao token API error: {}", errorBody);
                                    return Mono.error(new ResponseStatusException(
                                            clientResponse.statusCode(), "Failed to get Kakao token"));
                                })
                )
                .bodyToMono(KakaoTokenRes.class);
    }

    private Mono<KakaoIdTokenInfoRes> getKakaoUserInfo(KakaoTokenRes tokenRes) {
        String idToken = tokenRes.getId_token();
        if (idToken == null || idToken.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("ID token is null or empty"));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("id_token", idToken);

        return webClient.post()
                .uri(kakaoInfoUrl)
                .body(BodyInserters.fromFormData(params))
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("Kakao user info API error: {}", errorBody);
                                    return Mono.error(new ResponseStatusException(
                                            clientResponse.statusCode(), "Failed to get Kakao user info"));
                                })
                )
                .bodyToMono(KakaoIdTokenInfoRes.class);
    }

    private Mono<String> checkUserExistence(KakaoIdTokenInfoRes userInfo) {
        String kakaoUniqueMemberId = userInfo.getSub();
        if (kakaoUniqueMemberId == null || kakaoUniqueMemberId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Kakao user ID is null or empty"));
        }

        return Mono.fromCallable(() -> {
            Optional<User> existingUser = userRepository.findByKakaoUserNumber(kakaoUniqueMemberId);
            return existingUser.isPresent() ? "카카오 가입한적 있음" : "카카오 가입한적 없음";
        }).subscribeOn(Schedulers.boundedElastic());
    }
}