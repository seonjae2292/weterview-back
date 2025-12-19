package com.example.weterview.entity.studyGroup.vo;

import com.example.weterview.enums.ResultCode;
import com.example.weterview.exception.CustomException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 스터디 그룹 기간 정보 vo
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyPeriod {
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private StudyPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new CustomException(ResultCode.INVALID_DATE_RANGE);
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static StudyPeriod of(LocalDateTime startDate, LocalDateTime endDate) {
        return new StudyPeriod(startDate, endDate);
    }
}
