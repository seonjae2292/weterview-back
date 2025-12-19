package com.example.weterview.dto.common.response;

import com.example.weterview.entity.studyGroup.StudyGroup;
import com.example.weterview.enums.studyGroup.FieldEnum;
import com.example.weterview.enums.studyGroup.LocationEnum;
import com.example.weterview.enums.studyGroup.StatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) // 값이 null이면 JSON 필드 아예 제외
public class StudyGroupRes {
    private long id;
    private FieldEnum field;
    private StatusEnum status;
    private String title;
    private String subTitle;
    private LocationEnum location;
    private String description;
    private Integer currentMemberCount;
    private Integer maxMemberCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @JsonProperty("isLiked")
    private Boolean isLiked;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static StudyGroupRes from(StudyGroup studyGroup) {
        return StudyGroupRes.builder()
                .id(studyGroup.getId())
                .field(studyGroup.getContent().getField())
                .status(studyGroup.getStatus())
                .title(studyGroup.getContent().getTitle())
                .subTitle(studyGroup.getContent().getSubTitle())
                .location(studyGroup.getLocation())
                .description(studyGroup.getContent().getDescription())
                .currentMemberCount(studyGroup.getCapacity().getCurrentMemberCount())
                .maxMemberCount(studyGroup.getCapacity().getMaxMemberCount())
                .startDate(studyGroup.getPeriod().getStartDate())
                .endDate(studyGroup.getPeriod().getEndDate())
                .isLiked(null)
                .createdAt(studyGroup.getCreatedAt())
                .updatedAt(studyGroup.getUpdatedAt())
                .build();
    }

    public static StudyGroupRes of(StudyGroup studyGroup, boolean isLiked) {
        return StudyGroupRes.builder()
                .id(studyGroup.getId())
                .field(studyGroup.getContent().getField())
                .status(studyGroup.getStatus())
                .title(studyGroup.getContent().getTitle())
                .subTitle(studyGroup.getContent().getSubTitle())
                .location(studyGroup.getLocation())
                .description(studyGroup.getContent().getDescription())
                .currentMemberCount(studyGroup.getCapacity().getCurrentMemberCount())
                .maxMemberCount(studyGroup.getCapacity().getMaxMemberCount())
                .startDate(studyGroup.getPeriod().getStartDate())
                .endDate(studyGroup.getPeriod().getEndDate())
                .isLiked(isLiked)
                .createdAt(studyGroup.getCreatedAt())
                .updatedAt(studyGroup.getUpdatedAt())
                .build();
    }
}
