package com.example.weterview.dto.myPage.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetMyPageInfoRes {
    private LocalDateTime createdAt;
    private LocalDateTime UpdatedAt;
    private String kakaoEmail;
    private String nickname;
    private String gender;
}
