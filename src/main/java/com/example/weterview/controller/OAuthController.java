package com.example.weterview.controller;

import com.example.weterview.dto.SignupInfoDto;
import com.example.weterview.dto.common.ApiResponse;
import com.example.weterview.dto.common.OurMemberDto;
import com.example.weterview.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthService oAuthService;

    @GetMapping("/kakao/callback")
    public Mono<ApiResponse<? extends OurMemberDto>> getAuthorizeToken(@RequestParam("code") String code) {
        return oAuthService.postVerifyUserToKakao(code);
    }

    @GetMapping("/verify/duplicate/nickname")
    public ApiResponse<HashMap<String, Boolean>> isDuplicateNickname(@RequestParam("nickname") String nickname) {
        return oAuthService.isDuplicateNickname(nickname);
    }

    @PostMapping("/signup")
    public ApiResponse<?> signup(@RequestBody SignupInfoDto request) {
        return oAuthService.signup(request);
    }
}
