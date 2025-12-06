package com.example.weterview.dto.studyGroup.response;

import com.example.weterview.dto.myPage.response.GetJoinedStudyGroupRes;
import com.example.weterview.entity.StudyGroupMember;
import com.example.weterview.enums.studyMembership.JoinEnum;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetStudyGroupApplyMemberRes {
    private Long userId;
    private String kakaoUserNumber;
    private String nickname;
    private String kakaoEmail;
    private String gender;
    private String status;

    public static GetStudyGroupApplyMemberRes from(StudyGroupMember studyGroupMember) {
        return GetStudyGroupApplyMemberRes.builder()
                .userId(studyGroupMember.getUser().getId())
                .kakaoUserNumber(studyGroupMember.getUser().getKakaoUserNumber())
                .nickname(studyGroupMember.getUser().getNickname())
                .kakaoEmail(studyGroupMember.getUser().getKakaoEmail())
                .gender(studyGroupMember.getUser().getGender())
                .status(String.valueOf(studyGroupMember.getJoin()))
                .build();
    }

}
