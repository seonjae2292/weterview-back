package com.example.weterview.dto.studyGroup.request;

import com.example.weterview.entity.User;
import com.example.weterview.entity.studyGroup.StudyGroup;
import com.example.weterview.entity.studyGroup.vo.StudyCapacity;
import com.example.weterview.entity.studyGroup.vo.StudyContent;
import com.example.weterview.entity.studyGroup.vo.StudyPeriod;
import com.example.weterview.enums.studyGroup.FieldEnum;
import com.example.weterview.enums.studyGroup.LocationEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateStudyGroupReq {
    private String title;
    private String subTitle;
    private FieldEnum field;
    private String joinCondition;
    private String description;
    private String schedule;

    private Integer maxMemberCount;

    private String startDate;
    private String endDate;

    private LocationEnum location;

    public StudyGroup toEntity(User user) {
        StudyContent content = StudyContent.builder()
                .title(this.title)
                .subTitle(this.subTitle)
                .field(this.field)
                .joinCondition(this.joinCondition)
                .description(this.description)
                .schedule(this.schedule)
                .build();

        StudyPeriod period = StudyPeriod.of(
                LocalDateTime.parse(this.startDate),
                LocalDateTime.parse(this.endDate));

        StudyCapacity capacity = StudyCapacity.from(this.maxMemberCount);

        return StudyGroup.create(user, content, period, capacity, this.location);
    }
}
