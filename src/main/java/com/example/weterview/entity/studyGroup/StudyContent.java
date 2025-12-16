package com.example.weterview.entity.studyGroup;

import com.example.weterview.enums.studyGroup.FieldEnum;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyContent {
    private String title;
    private String subTitle;
    private String description;
    @Enumerated(EnumType.STRING)
    private FieldEnum field;

    public StudyContent(String title, String subTitle, String description, FieldEnum field) {
        this.title = title;
        this.subTitle = subTitle;
        this.description = description;
        this.field = field;
    }
}
