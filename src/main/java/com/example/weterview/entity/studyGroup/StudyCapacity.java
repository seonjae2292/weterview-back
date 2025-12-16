package com.example.weterview.entity.studyGroup;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 스터디 그룹 인원 정보 vo
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyCapacity {
    private int currentMemberCount;
    private int maxMemberCount;

    public StudyCapacity(int maxMemberCount) {
        this.currentMemberCount = 1;
        this.maxMemberCount = maxMemberCount;
    }

    public boolean isFull() {
        return currentMemberCount >= maxMemberCount;
    }
}
