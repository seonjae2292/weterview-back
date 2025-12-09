package com.example.weterview.dto.studyGroup.response;

import com.example.weterview.entity.StudyGroupMember;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyGroupApplyMemberRes {
    private Long userId;
    private String kakaoUserNumber;
    private String nickname;
    private String kakaoEmail;
    private String gender;
    private String status;

    public static StudyGroupApplyMemberRes from(StudyGroupMember studyGroupMember) {
        return StudyGroupApplyMemberRes.builder()
                .userId(studyGroupMember.getUser().getId())
                .kakaoUserNumber(studyGroupMember.getUser().getKakaoUserNumber())
                .nickname(studyGroupMember.getUser().getNickname())
                .kakaoEmail(studyGroupMember.getUser().getKakaoEmail())
                .gender(studyGroupMember.getUser().getGender())
                .status(String.valueOf(studyGroupMember.getJoin()))
                .build();
    }

}
