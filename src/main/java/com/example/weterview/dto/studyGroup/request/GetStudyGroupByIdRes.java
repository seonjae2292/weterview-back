package com.example.weterview.dto.studyGroup.request;

import com.example.weterview.enums.studyGroup.FieldEnum;
import com.example.weterview.enums.studyGroup.LocationEnum;
import com.example.weterview.enums.studyGroup.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class GetStudyGroupByIdRes {
    private String id;
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
}
