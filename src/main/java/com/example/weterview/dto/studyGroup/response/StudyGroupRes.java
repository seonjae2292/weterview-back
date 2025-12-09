package com.example.weterview.dto.studyGroup.response;

import com.example.weterview.entity.StudyGroup;
import com.example.weterview.enums.studyGroup.FieldEnum;
import com.example.weterview.enums.studyGroup.LocationEnum;
import com.example.weterview.enums.studyGroup.StatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
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

    public static StudyGroupRes from(StudyGroup entity) {
        StudyGroupRes dto = new StudyGroupRes();
        dto.id = entity.getId();
        dto.title = entity.getTitle();
        dto.subTitle = entity.getSubTitle();
        dto.description = entity.getDescription();

        dto.recruitingNumber = entity.getRecruitingNumber();
        dto.totalNumber = entity.getTotalNumber();

        dto.field = entity.getField();
        dto.status = entity.getStatus();
        dto.location = entity.getLocation();

        dto.startDate = entity.getStartDate();
        dto.endDate = entity.getEndDate();

        return dto;
    }
}
