package com.example.weterview.controller;

import com.example.weterview.dto.common.request.SignupInfoReq;
import com.example.weterview.dto.common.ApiResponse;
import com.example.weterview.dto.common.response.KakaoLoginRes;
import com.example.weterview.dto.common.response.KakaoLoginSuccessRes;
import com.example.weterview.enums.ResultCode;
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
    public Mono<ApiResponse<? extends KakaoLoginRes>> getAuthorizeToken(@RequestParam("code") String code) {
        return oAuthService.postVerifyUserToKakao(code);
    }

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ApiResponse<KakaoLoginSuccessRes> signup(@RequestBody SignupInfoReq request) {
        KakaoLoginSuccessRes result = oAuthService.signup(request);
        return ApiResponse.success(result);
    }

    // 닉네임 중복 확인
    @GetMapping("/verify/duplicate/nickname")
    public ApiResponse<HashMap<String, Boolean>> isDuplicateNickname(@RequestParam("nickname") String nickname) {
        boolean existsNickname = oAuthService.isDuplicateNickname(nickname);

        if (existsNickname) {
            return ApiResponse.of(ResultCode.DUPLICATE_NICKNAME);
        }

        return ApiResponse.success();
    }
}
