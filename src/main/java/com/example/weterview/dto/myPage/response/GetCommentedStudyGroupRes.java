package com.example.weterview.dto.myPage.response;

import com.example.weterview.entity.StudyGroupComment;
import com.example.weterview.enums.studyGroup.FieldEnum;
import com.example.weterview.enums.studyGroup.LocationEnum;
import com.example.weterview.enums.studyGroup.StatusEnum;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class GetCommentedStudyGroupRes {
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

    public static GetCommentedStudyGroupRes from(StudyGroupComment studyGroupComment) {
        return GetCommentedStudyGroupRes.builder()
                .studyGroupId(studyGroupComment.getStudyGroup().getId())
                .title(studyGroupComment.getStudyGroup().getTitle())
                .subTitle(studyGroupComment.getStudyGroup().getSubTitle())
                .description(studyGroupComment.getStudyGroup().getDescription())
                .schedule(studyGroupComment.getStudyGroup().getSchedule())
                .joinCondition(studyGroupComment.getStudyGroup().getJoinCondition())
                .contact(studyGroupComment.getStudyGroup().getContact())
                .recruitingNumber(studyGroupComment.getStudyGroup().getRecruitingNumber())
                .totalNumber(studyGroupComment.getStudyGroup().getTotalNumber())
                .field(studyGroupComment.getStudyGroup().getField())
                .status(studyGroupComment.getStudyGroup().getStatus())
                .location(studyGroupComment.getStudyGroup().getLocation())
                .startDate(studyGroupComment.getStudyGroup().getStartDate())
                .endDate(studyGroupComment.getStudyGroup().getEndDate())
                .createdAt(studyGroupComment.getStudyGroup().getCreatedAt())
                .updatedAt(studyGroupComment.getStudyGroup().getUpdatedAt())
                .deletedAt(studyGroupComment.getStudyGroup().getDeletedAt())
                .build();
    }
}
