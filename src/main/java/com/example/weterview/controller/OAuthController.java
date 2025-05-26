package com.example.weterview.controller;

import com.example.weterview.dto.common.ApiResponse;
import com.example.weterview.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthService oAuthService;

    @GetMapping("/kakao/callback")
    public Mono<ApiResponse<?>> getAuthorizeToken(@RequestParam("code") String code) {
        return oAuthService.postVerifyUserToKakao(code);
    }
}