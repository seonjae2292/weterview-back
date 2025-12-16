package com.example.weterview.entity.studyGroup.vo;

import com.example.weterview.enums.studyGroup.FieldEnum;
import com.example.weterview.enums.studyGroup.StatusEnum;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyContent {
    private String title;

    private String subTitle;

    private String description;

    @Comment(value = "스터디 상세 일정 ex) 매주 토요일 오후 2시 ~ 5시")
    private String schedule;

    @Comment(value = "스터디에 참가할 수 있는 조건 ex) java에 대한 기본기가 있으면 좋겠습니다.")
    private String joinCondition;

    @Comment(value = "연락할 수 있는 방법 ")
    private String contact;

    @Enumerated(EnumType.STRING)
    private FieldEnum field;

    public StudyContent(String title, String subTitle, String description,
                        String schedule, String joinCondition, String contact, FieldEnum field) {
        this.title = title;
        this.subTitle = subTitle;
        this.description = description;
        this.schedule = schedule;
        this.joinCondition = joinCondition;
        this.contact = contact;
        this.field = field;
    }
}
