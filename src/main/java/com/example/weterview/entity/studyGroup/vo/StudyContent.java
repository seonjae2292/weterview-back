package com.example.weterview.entity.studyGroup.vo;

import com.example.weterview.enums.ResultCode;
import com.example.weterview.enums.studyGroup.FieldEnum;
import com.example.weterview.enums.studyGroup.StatusEnum;
import com.example.weterview.exception.CustomException;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Comment(value = "스터디에 참가할 수 있는 조건 ex) java에 대한 기본기가 있으면 좋겠습니다.")
    private String joinCondition;

    @Enumerated(EnumType.STRING)
    private FieldEnum field;

    @Comment(value = "스터디 그룹 스케줄 상세")
    private String schedule;

    @Builder
    public StudyContent(String title, String subTitle, String description,
                        String joinCondition, String schedule, FieldEnum field) {
        if (title == null || title.isBlank()) {
            throw new CustomException(ResultCode.INVALID_INPUT_VALUE, "제목은 필수입니다.");
        }
        if (description == null || description.isBlank()) {
            throw new CustomException(ResultCode.INVALID_INPUT_VALUE, "내용은 필수입니다.");
        }

        this.title = title;
        this.subTitle = subTitle;
        this.description = description;
        this.joinCondition = joinCondition;
        this.schedule = schedule;
        this.field = field;
    }
}
