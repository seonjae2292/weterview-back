package com.example.weterview.dto;

import lombok.Data;

@Data
public class KakaoIdTokenInfoRes {
    private String sub; // ID 토큰에 해당하는 사용자의 회원번호
    private String email; // kakao
}

/// https://developers.kakao.com/docs/latest/ko/kakaologin/utilize
