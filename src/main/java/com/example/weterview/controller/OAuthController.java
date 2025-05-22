package com.example.weterview.controller;

import com.example.weterview.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthService oAuthService;

    @GetMapping("/kakao/callback")
    public Mono<ResponseEntity<String>> getAuthorizeToken(@RequestParam("code") String code) {
        return oAuthService.postVerifyUserToKakao(code)
                .map(result -> ResponseEntity.ok(result));
    }
}