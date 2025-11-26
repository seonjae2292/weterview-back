package com.example.weterview.dto.myPage.response;

import com.example.weterview.entity.StudyGroupLike;
import com.example.weterview.enums.studyGroup.FieldEnum;
import com.example.weterview.enums.studyGroup.LocationEnum;
import com.example.weterview.enums.studyGroup.StatusEnum;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class GetLikedPostRes {
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

    public static GetLikedPostRes from(StudyGroupLike studyGroupLike) {
        return GetLikedPostRes.builder()
                .studyGroupId(studyGroupLike.getStudyGroup().getId())
                .title(studyGroupLike.getStudyGroup().getTitle())
                .subTitle(studyGroupLike.getStudyGroup().getSubTitle())
                .description(studyGroupLike.getStudyGroup().getDescription())
                .schedule(studyGroupLike.getStudyGroup().getSchedule())
                .joinCondition(studyGroupLike.getStudyGroup().getJoinCondition())
                .contact(studyGroupLike.getStudyGroup().getContact())
                .recruitingNumber(studyGroupLike.getStudyGroup().getRecruitingNumber())
                .totalNumber(studyGroupLike.getStudyGroup().getTotalNumber())
                .field(studyGroupLike.getStudyGroup().getField())
                .status(studyGroupLike.getStudyGroup().getStatus())
                .location(studyGroupLike.getStudyGroup().getLocation())
                .startDate(studyGroupLike.getStudyGroup().getStartDate())
                .endDate(studyGroupLike.getStudyGroup().getEndDate())
                .createdAt(studyGroupLike.getStudyGroup().getCreatedAt())
                .updatedAt(studyGroupLike.getStudyGroup().getUpdatedAt())
                .deletedAt(studyGroupLike.getStudyGroup().getDeletedAt())
                .build();
    }
}
