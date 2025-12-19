package com.example.weterview.dto.studyGroup.request;

import com.example.weterview.entity.studyGroup.vo.StudyContent;
import com.example.weterview.entity.studyGroup.vo.StudyPeriod;
import com.example.weterview.enums.studyGroup.FieldEnum;
import com.example.weterview.enums.studyGroup.LocationEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateStudyGroupReq {
    private FieldEnum field;
    private String title;
    private String subTitle;
    private Integer recruitingNumber;
    private String startDate;
    private String endDate;
    private LocationEnum location;
    private String description;
    private String schedule;
    private String joinCondition;
    private String contact;

    public StudyContent toContent(StudyContent current) {
        return StudyContent.builder()
                .title(this.title != null ? this.title : current.getTitle())
                .subTitle(this.subTitle != null ? this.subTitle : current.getSubTitle())
                .description(this.description != null ? this.description : current.getDescription())
                .schedule(this.schedule != null ? this.schedule : current.getSchedule())
                .joinCondition(this.joinCondition != null ? this.joinCondition : current.getJoinCondition())
                .contact(this.contact != null ? this.contact : current.getContact())
                .field(this.field != null ? this.field : current.getField())
                .build();
    }

    public StudyPeriod toPeriod() {
        // 날짜가 둘 다 있을 때만 기간 변경 의도로 파악
        if (this.startDate == null || this.endDate == null) {
            return null;
        }
        return StudyPeriod.of(
                LocalDateTime.parse(this.startDate),
                LocalDateTime.parse(this.endDate)
        );
    }


}
