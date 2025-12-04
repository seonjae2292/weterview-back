package com.example.weterview.dto.myPage.response;

import com.example.weterview.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyPageRes {
    private String kakaoEmail;
    private String nickname;
    private String gender;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MyPageRes from(User user) {
        return MyPageRes.builder()
                .kakaoEmail(user.getKakaoEmail())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
