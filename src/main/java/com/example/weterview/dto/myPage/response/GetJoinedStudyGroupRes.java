package com.example.weterview.dto.myPage.response;

import com.example.weterview.entity.StudyGroup;
import com.example.weterview.entity.StudyGroupMember;
import com.example.weterview.enums.studyGroup.FieldEnum;
import com.example.weterview.enums.studyGroup.LocationEnum;
import com.example.weterview.enums.studyGroup.StatusEnum;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@RequiredArgsConstructor
public class GetJoinedStudyGroupRes {
    private Long studyGroupId;
    private String title;
    private String subTitle;
    private String description;
    private String schedule;
    private String joinCondition;
    private String contact;

    private Integer recruitingNumber;
    private Integer totalNumber;

    private FieldEnum field;
    private StatusEnum status;
    private LocationEnum location;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static GetJoinedStudyGroupRes from(StudyGroupMember member) {
        return GetJoinedStudyGroupRes.builder()
                .studyGroupId(member.getStudyGroup().getId())
                .title(member.getStudyGroup().getTitle())
                .subTitle(member.getStudyGroup().getSubTitle())
                .description(member.getStudyGroup().getDescription())
                .schedule(member.getStudyGroup().getSchedule())
                .joinCondition(member.getStudyGroup().getJoinCondition())
                .contact(member.getStudyGroup().getContact())
                .recruitingNumber(member.getStudyGroup().getRecruitingNumber())
                .totalNumber(member.getStudyGroup().getTotalNumber())
                .field(member.getStudyGroup().getField())
                .status(member.getStudyGroup().getStatus())
                .location(member.getStudyGroup().getLocation())
                .startDate(member.getStudyGroup().getStartDate())
                .endDate(member.getStudyGroup().getEndDate())
                .createdAt(member.getStudyGroup().getCreatedAt())
                .updatedAt(member.getStudyGroup().getUpdatedAt())
                .deletedAt(member.getStudyGroup().getDeletedAt())
                .build();
    }
}
