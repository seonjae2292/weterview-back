package com.example.weterview.dto.studyGroup.response;

import com.example.weterview.enums.studyGroup.FieldEnum;
import com.example.weterview.enums.studyGroup.LocationEnum;
import com.example.weterview.enums.studyGroup.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class GetStudyGroupPageRes {
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
}
