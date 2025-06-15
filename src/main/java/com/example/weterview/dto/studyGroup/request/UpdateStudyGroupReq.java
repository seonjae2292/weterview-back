package com.example.weterview.dto.studyGroup.request;

import com.example.weterview.enums.studyGroup.FieldEnum;
import com.example.weterview.enums.studyGroup.LocationEnum;
import lombok.Data;

@Data
public class UpdateStudyGroupReq {
    private FieldEnum field;
    private String title;
    private String subTitle;
    private Integer recruitingNumber;
    private Integer totalNumber;
    private String startDate;
    private String endDate;
    private LocationEnum location;
    private String description;
    private String schedule;
    private String joinCondition;
    private String contact;
}
