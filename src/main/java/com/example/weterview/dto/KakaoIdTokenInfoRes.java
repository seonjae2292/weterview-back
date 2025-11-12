package com.example.weterview.dto;

import lombok.Data;

@Data
public class KakaoIdTokenInfoRes {
    private String sub;
    /** UNIX Timestamp */
    /// https://developers.kakao.com/docs/latest/ko/kakaologin/utilize
    private Integer authTime;
    private String nickname;
    private String email;
}
