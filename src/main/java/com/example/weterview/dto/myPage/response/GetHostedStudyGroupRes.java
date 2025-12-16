package com.example.weterview.dto.myPage.response;

import com.example.weterview.entity.studyGroup.StudyGroup;
import com.example.weterview.enums.studyGroup.FieldEnum;
import com.example.weterview.enums.studyGroup.LocationEnum;
import com.example.weterview.enums.studyGroup.StatusEnum;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class GetHostedStudyGroupRes {
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

    // DTO 내부에 변환을 책임지는 로직을 만드는 것
    // 이 생성자의 "생성"의 목적을 더 명확하게 나타내는 방법
    public static GetHostedStudyGroupRes from(StudyGroup entity) {
        GetHostedStudyGroupRes dto = new GetHostedStudyGroupRes();
        dto.studyGroupId = entity.getId();
        dto.title = entity.getTitle();
        dto.subTitle = entity.getSubTitle();
        dto.description = entity.getDescription();
        dto.schedule = entity.getSchedule();
        dto.joinCondition = entity.getJoinCondition();
        dto.contact = entity.getContact();

        dto.recruitingNumber = entity.getRecruitingNumber();
        dto.totalNumber = entity.getTotalNumber();

        dto.field = entity.getField();
        dto.status = entity.getStatus();
        dto.location = entity.getLocation();

        dto.startDate = entity.getStartDate();
        dto.endDate = entity.getEndDate();

        dto.createdAt = entity.getCreatedAt();
        dto.updatedAt = entity.getUpdatedAt();
        dto.deletedAt = entity.getDeletedAt();
        return dto;
    }
}
