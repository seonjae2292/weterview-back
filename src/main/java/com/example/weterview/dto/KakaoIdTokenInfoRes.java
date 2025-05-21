package com.example.weterview.dto;

import lombok.Data;

@Data
public class KakaoIdTokenInfoRes {
    private String sub;
    /** UNIX Timestamp */
    private Integer authTime;
}
