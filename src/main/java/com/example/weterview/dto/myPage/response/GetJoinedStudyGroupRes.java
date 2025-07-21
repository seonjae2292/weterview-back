package com.example.weterview.dto.myPage.response;

import com.example.weterview.enums.studyGroup.FieldEnum;
import com.example.weterview.enums.studyGroup.LocationEnum;
import com.example.weterview.enums.studyGroup.StatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetJoinedStudyGroupRes {
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
}
