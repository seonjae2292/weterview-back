package com.example.weterview.dto.studyGroup.response;

import com.example.weterview.enums.studyMembership.JoinEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetStudyGroupApplyMemberRes {
    private Long userId;
    private String kakaoUserNumber;
    private String nickname;
    private String kakaoEmail;
    private String gender;
    private LocalDateTime createdAt;
    private String status;
}
