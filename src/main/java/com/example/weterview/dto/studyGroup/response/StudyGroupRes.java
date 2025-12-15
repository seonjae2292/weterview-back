package com.example.weterview.dto.studyGroup.response;

import com.example.weterview.entity.StudyGroup;
import com.example.weterview.enums.studyGroup.FieldEnum;
import com.example.weterview.enums.studyGroup.LocationEnum;
import com.example.weterview.enums.studyGroup.StatusEnum;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyGroupRes {
    private long id;
    private FieldEnum field;
    private StatusEnum status;
    private String title;
    private String subTitle;
    private LocationEnum location;
    private String description;
    private Integer recruitingNumber;
    private Integer totalNumber;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public static StudyGroupRes from(StudyGroup studyGroup) {
        return StudyGroupRes.builder()
                .id(studyGroup.getId())
                .field(studyGroup.getField())
                .status(studyGroup.getStatus())
                .title(studyGroup.getTitle())
                .subTitle(studyGroup.getSubTitle())
                .location(studyGroup.getLocation())
                .description(studyGroup.getDescription())
                .recruitingNumber(studyGroup.getRecruitingNumber())
                .totalNumber(studyGroup.getTotalNumber())
                .startDate(studyGroup.getStartDate())
                .endDate(studyGroup.getEndDate())
                .build();
    }

}
