package com.example.weterview.entity.studyGroup;

import com.example.weterview.entity.BaseTimeEntity;
import com.example.weterview.entity.User;
import com.example.weterview.entity.studyGroup.vo.StudyCapacity;
import com.example.weterview.entity.studyGroup.vo.StudyContent;
import com.example.weterview.entity.studyGroup.vo.StudyPeriod;
import com.example.weterview.enums.ResultCode;
import com.example.weterview.enums.studyGroup.FieldEnum;
import com.example.weterview.enums.studyGroup.LocationEnum;
import com.example.weterview.enums.studyGroup.StatusEnum;
import com.example.weterview.exception.CustomException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/// 스터디 그룹 게시글 테이블
@Entity
@Table(name = "study_groups")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class StudyGroup extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_group_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Embedded
    private StudyContent content;

    @Embedded
    private StudyPeriod period;

    @Embedded
    private StudyCapacity capacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "location")
    @Comment(value = "오프라인 스터디 그룹 위치")
    private LocationEnum location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Comment(value = "모집중 or 모집완료 or 삭제됨")
    private StatusEnum status;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Lombok에게 "이 생성자를 사용하는 빌더 클래스를 만들어줘"라고
    @Builder(access = AccessLevel.PRIVATE)
    public StudyGroup(User user, StudyContent content, StudyPeriod period, StudyCapacity capacity, LocationEnum location) {
        this.user = user;
        this.content = content;
        this.period = period;
        this.capacity = capacity;
        this.location = location;
        this.status = StatusEnum.RECRUITING;
    }

    // 빌더를 바로 열어두면 실수로 content를 빼먹고 .build()를 호출할 수 있지만, 메서드 파라미터로 받으면 컴파일 에러가 나므로 실수를 원천 봉쇄
    public static StudyGroup create(User user, StudyContent content,
                                    StudyPeriod period, StudyCapacity capacity,
                                    LocationEnum location) {
        return StudyGroup.builder()
                .user(user)
                .content(content)
                .period(period)
                .location(location)
                .capacity(capacity)
                .build();
    }

    /**
     * 기본 정보 수정 (제목, 내용, 분야 등)
     * - 수정 권한 검증이나 상태 검증을 선행
     */
    public void updateInfo(StudyContent mergedContent, LocationEnum newLocation) {
        validateEditable();
        this.content = mergedContent;
        this.location = newLocation;
    }

    /**
     * 일정 변경 (기간)
     * - 시작일, 종료일의 선후 관계 검증은 StudyPeriod 생성자에게 위임하거나 여기서 처리
     */
    public void reschedule(StudyPeriod period) {
        validateEditable();
        this.period = period;
    }

    /**
     * 모집 인원 수정
     * - 현재 참여 인원보다 적게 설정할 수 없음
     */
    public void updateRecruitment(int newRecruitingNumber) {
        validateEditable();
        if (newRecruitingNumber < this.capacity.getCurrentMemberCount()) {
            throw new CustomException(ResultCode.INVALID_RECRUITMENT_NUMBER, "현재 참여 인원보다 적게 설정할 수 없습니다.");
        }
        this.capacity = StudyCapacity.from(newRecruitingNumber);
    }

    /**
     * 스터디 그룹 삭제 (Soft Delete)
     */
    public void delete() {
        if (this.status == StatusEnum.DELETED) {
            throw new CustomException(ResultCode.ALREADY_DELETED_STUDY_GROUP);
        }
        this.status = StatusEnum.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    private void validateEditable() {
        if (this.status == StatusEnum.DELETED) {
            throw new CustomException(ResultCode.ALREADY_DELETED_STUDY_GROUP);
        }
        if (this.status == StatusEnum.CLOSED) {
            // 필요하다면 마감된 스터디도 수정 못하게 막을 수 있음
            // throw new CustomException(ResultCode.ALREADY_CLOSED_STUDY_GROUP);
        }
    }
}