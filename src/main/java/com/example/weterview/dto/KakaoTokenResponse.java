package com.example.weterview.dto;

import lombok.Data;

@Data
public class KakaoTokenResponse {
    private String token_type;
    private String access_token;
    private String refresh_token;
    private String scope;
    private String id_token;

    private long expires_in;
    private long refresh_token_expires_in;
}

