package com.example.weterview.dto.myPage.response;

import com.example.weterview.entity.StudyGroupComment;
import com.example.weterview.enums.studyGroup.FieldEnum;
import com.example.weterview.enums.studyGroup.LocationEnum;
import com.example.weterview.enums.studyGroup.StatusEnum;
import lombok.*;

import java.time.LocalDateTime;

@Getter
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

    private Integer currentMemberCount;
    private Integer maxMemberCount;

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
                .title(studyGroupComment.getStudyGroup().getContent().getTitle())
                .subTitle(studyGroupComment.getStudyGroup().getContent().getSubTitle())
                .description(studyGroupComment.getStudyGroup().getContent().getDescription())
                .schedule(studyGroupComment.getStudyGroup().getContent().getSchedule())
                .joinCondition(studyGroupComment.getStudyGroup().getContent().getJoinCondition())
                .currentMemberCount(studyGroupComment.getStudyGroup().getCapacity().getCurrentMemberCount())
                .maxMemberCount(studyGroupComment.getStudyGroup().getCapacity().getMaxMemberCount())
                .field(studyGroupComment.getStudyGroup().getContent().getField())
                .status(studyGroupComment.getStudyGroup().getStatus())
                .location(studyGroupComment.getStudyGroup().getLocation())
                .startDate(studyGroupComment.getStudyGroup().getPeriod().getStartDate())
                .endDate(studyGroupComment.getStudyGroup().getPeriod().getEndDate())
                .createdAt(studyGroupComment.getStudyGroup().getCreatedAt())
                .updatedAt(studyGroupComment.getStudyGroup().getUpdatedAt())
                .deletedAt(studyGroupComment.getStudyGroup().getDeletedAt())
                .build();
    }
}
