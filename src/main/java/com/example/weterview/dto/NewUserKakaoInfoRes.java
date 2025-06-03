package com.example.weterview.dto;

import lombok.*;

@Data
@RequiredArgsConstructor
public class NewUserKakaoInfoRes {
    private String kakaoUniqueId;
    private String kakaoEmail;
}
