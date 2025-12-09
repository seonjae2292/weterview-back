package com.example.weterview.dto.studyGroup.request;

import com.example.weterview.entity.StudyGroup;
import com.example.weterview.enums.studyGroup.FieldEnum;
import com.example.weterview.enums.studyGroup.LocationEnum;
import com.example.weterview.enums.studyGroup.StatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetStudyGroupByIdRes {
    private Long id;
    private FieldEnum field;
    private StatusEnum status;
    private String title;
    private String subTitle;
    private Integer recruitingNumber;
    private Integer totalNumber;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocationEnum location;
    private String description;
    private String schedule;
    private String joinCondition;
    private String contact;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @JsonProperty("isLiked")
    private boolean isLiked = false;

    public static GetStudyGroupByIdRes from(StudyGroup studyGroup, boolean isLiked) {
        return GetStudyGroupByIdRes.builder()
                .id(studyGroup.getId())
                .field(studyGroup.getField())
                .status(studyGroup.getStatus())
                .title(studyGroup.getTitle())
                .subTitle(studyGroup.getSubTitle())
                .recruitingNumber(studyGroup.getRecruitingNumber())
                .totalNumber(studyGroup.getTotalNumber())
                .startDate(studyGroup.getStartDate())
                .endDate(studyGroup.getEndDate())
                .location(studyGroup.getLocation())
                .description(studyGroup.getDescription())
                .schedule(studyGroup.getSchedule())
                .joinCondition(studyGroup.getJoinCondition())
                .contact(studyGroup.getContact())
                .createdAt(studyGroup.getCreatedAt())
                .updatedAt(studyGroup.getUpdatedAt())
                .isLiked(isLiked)
                .build();
    }
}
