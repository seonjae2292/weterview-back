package com.example.weterview.dto.common.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class KakaoLoginSuccessRes extends KakaoLoginRes {
    private String accessToken;

    private KakaoLoginSuccessRes(String accessToken) {
        super(true);
        this.accessToken = accessToken;
    }

    public static KakaoLoginSuccessRes create(String accessToken) {
        return new KakaoLoginSuccessRes(accessToken);
    }
}
