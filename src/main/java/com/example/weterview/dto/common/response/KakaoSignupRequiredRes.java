package com.example.weterview.dto.common.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class KakaoSignupRequiredRes extends KakaoLoginRes {
    private String kakaoUserNumber;
    private String kakaoEmail;

    private KakaoSignupRequiredRes(String kakaoUserNumber, String kakaoEmail) {
        super(false);
        this.kakaoUserNumber = kakaoUserNumber;
        this.kakaoEmail = kakaoEmail;
    }

    public static KakaoSignupRequiredRes create(String kakaoUserNumber, String kakaoEmail) {
        return new KakaoSignupRequiredRes(kakaoUserNumber, kakaoEmail);
    }
}
