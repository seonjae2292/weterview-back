package com.example.weterview.service;

import com.example.weterview.dto.*;
import com.example.weterview.dto.common.ApiResponse;

import com.example.weterview.dto.common.response.NewUserKakaoInfoRes;
import com.example.weterview.dto.common.response.OurMemberDto;
import com.example.weterview.dto.common.response.SignupRes;
import com.example.weterview.dto.common.request.SignupInfoReq;
import com.example.weterview.entity.User;
import com.example.weterview.enums.ResultCode;
import com.example.weterview.repository.UserRepository;
import com.example.weterview.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.*;

import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {
    private final WebClient webClient;
    private final UserRepository userRepository;
    private final PasswordEncoder pwEncoder;
    private final JwtUtil jwtUtil;

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

    public Mono<ApiResponse<? extends OurMemberDto>> postVerifyUserToKakao(String code) {
        return getKakaoToken(code)
                .flatMap(this::getKakaoUserInfo)
                .flatMap(kakaoInfoFromIdToken -> {
                    String kakaoUserNumber = kakaoInfoFromIdToken.getSub();
                    String kakaoEmail = kakaoInfoFromIdToken.getEmail();

                    return doesUserExistOurService(kakaoUserNumber)
                            .flatMap(isOurService -> {
                                if (isOurService) {
                                    SignupRes signupRes = new SignupRes();
                                    String accessToken = jwtUtil.generateAccessToken(kakaoUserNumber, kakaoUserNumber);

                                    signupRes.setAccessToken(accessToken);
                                    signupRes.setOurMember(true);

                                    return Mono.just(ApiResponse.of(ResultCode.REGISTERED_MEMBER, signupRes));
                                } else {
                                    /// 소셜 로그인 시에 새로운 사용자면 이 시점에 DB에 바로 저장하지 않고
                                    /// isOutMember 값을 false로 응답 주게 되면 프론트에서 추가 정보
                                    /// 입력 후에 DB에 저장 될 수 있게 할 것
                                    NewUserKakaoInfoRes newUserKakaoInfoRes = new NewUserKakaoInfoRes();

                                    newUserKakaoInfoRes.setKakaoUniqueId(kakaoUserNumber);
                                    newUserKakaoInfoRes.setKakaoEmail(kakaoEmail);
                                    newUserKakaoInfoRes.setOurMember(false);

                                    return Mono.just(ApiResponse.of(ResultCode.USER_NOT_FOUND, newUserKakaoInfoRes));
                                }
                            });
                })
                .doOnError(error -> log.error("OAuth verification failed: ", error));
    }

    /**
     * 프론트에서 전달받은 인가코드를 카카오 서버에 전달하여 토큰 받기
     * @param code 프론트에서 전달받은 인가코드
     * @return
     */
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

    /**
     * id_token을 받아 카카오 서버에 토큰에 들어있는 정보를 확인하기 위한 API 요청
     * @param tokenRes id_token 사용
     * @return
     */
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

    /**
     * 고유회원번호가 디비에 있는지 조회
     * @param memberUniqueId 고유회원번호
     * @return
     */
    private Mono<Boolean> doesUserExistOurService(String memberUniqueId) {
        if (memberUniqueId == null || memberUniqueId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Kakao user ID is null or empty"));
        }
        return Mono.fromCallable(() -> userRepository.findByKakaoUserNumber(memberUniqueId).isPresent())
                .subscribeOn(Schedulers.boundedElastic());
        // 구독이 일어나야 실행된다.
        // 내부의 람다가 실행되는 스레드를 Schedulers.boundedElastic()에서 가져오도록 지정
        // 실제 DB 조회는 boundedElastic 스레드 풀의 한 스레드에서 수행된다.
        // 이동안 원래의 스테드(예 : http 요청을 처리하던 Netty 이벤트 루프 스레드)는 다른 작업을 계속 처리할 수 있게 된다.
        // 즉, 직접 수행하는것이 아닌 외주를 맡긴다고 생각
    }

    // 추가 정보 입력 -> 회원가입
    public boolean signup(SignupInfoReq userInfo) {
        boolean isMember = userRepository.existsByKakaoUserNumber(userInfo.getKakaoUserNumber());

        if (isMember) {
            return false;
        }
        User newUser = User.create(
                userInfo.getKakaoUserNumber(), userInfo.getNickname(),
                userInfo.getKakaoEmail(), userInfo.getGender());
        userRepository.save(newUser);

        return true;
    }

    public boolean isDuplicateNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}