package com.example.weterview.dto.studyGroup.request;

import lombok.Data;

@Data
public class CreateStudyGroupReq {
    private String category;
    private String title;
    private String subTitle;
    private Integer recruitingNumber;
    private Integer totalNumber;
    private String startDate;
    private String endDate;
    private String location;
    private String description;
    private String schedule;
    private String joinCondition;
    private String contact;
}
