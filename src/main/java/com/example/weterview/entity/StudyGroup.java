package com.example.weterview.entity;

import com.example.weterview.enums.ErrorCode;
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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/// 스터디 그룹 게시글 테이블
@Entity
@Table(name = "study_groups")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class StudyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_group_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "field", nullable = false)
    @Comment(value = "분야")
    private FieldEnum field;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Comment(value = "모집중 or 모집완료 or 삭제됨")
    private StatusEnum status;

    @Column(name = "title", nullable = false)
    @Comment(value = "제목")
    private String title;

    @Column(name = "sub_title", nullable = false)
    @Comment(value = "부제목")
    private String subTitle;

    @Column(name = "recruiting_number", nullable = false)
    @Comment(value = "현재 참가한 사용자 수")
    private Integer recruitingNumber;

    @Column(name = "total_number", nullable = false)
    @Comment(value = "총 참가 가능한 사용자 수")
    private Integer totalNumber;

    @Column(name = "start_date", nullable = false)
    @Comment(value = "스터디 그룹 시작 날짜")
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    @Comment(value = "스터디 그룹 끝나는 날짜")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "location", nullable = false)
    @Comment(value = "오프라인 스터디 그룹 위치")
    private LocationEnum location;

    @Column(name = "description", nullable = false)
    @Comment(value = "상세설명")
    private String description;

    @Column(name = "schedule", nullable = false)
    @Comment(value = "스터디 상세 일정 ex) 매주 토요일 오후 2시 ~ 5시")
    private String schedule;

    @Column(name = "join_condition", nullable = false)
    @Comment(value = "스터디에 참가할 수 있는 조건 ex) java에 대한 기본기가 있으면 좋겠습니다.")
    private String joinCondition;

    @Column(name = "contact", nullable = false)
    @Comment(value = "연락할 수 있는 방법 ")
    private String contact;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    private StudyGroup(User user, FieldEnum field, StatusEnum status, String title,
                      String subTitle, Integer recruitingNumber, Integer totalNumber,
                      LocalDateTime startDate, LocalDateTime endDate, LocationEnum location,
                      String description, String schedule, String joinCondition, String contact) {

        if (user == null || field == null || title == null || subTitle == null ||
                recruitingNumber == null || totalNumber == null ||
                startDate == null || endDate == null || location == null ||
                description == null || schedule == null || joinCondition == null || contact == null) {
            throw new CustomException(ErrorCode.MISSING_INPUT_VALUE);
        }

        validateDate(startDate, endDate);
        validateRecruitment(recruitingNumber, totalNumber);

        // 3. 값 할당
        this.user = user;
        this.field = field;
        this.title = title;
        this.subTitle = subTitle;
        this.recruitingNumber = recruitingNumber;
        this.totalNumber = totalNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.description = description;
        this.schedule = schedule;
        this.joinCondition = joinCondition;
        this.contact = contact;

        // 4. 기본값 설정 (Optional 값 처리)
        // status는 안 들어오면 '모집중'으로 설정
        this.status = (status != null) ? status : StatusEnum.RECRUITING;
    }

    /**
     * 기본 정보 수정 (제목, 설명, 분야, 장소 등)
     * - 변경이 필요한 항목만 null이 아닌 값으로 넘어옴
     */
    public void updateInfo(String title, String subTitle, FieldEnum field,
                           LocationEnum location, String description,
                           String joinCondition, String contact) {
        if (title != null) this.title = title;
        if (subTitle != null) this.subTitle = subTitle;
        if (field != null) this.field = field;
        if (location != null) this.location = location;
        if (description != null) this.description = description;
        if (joinCondition != null) this.joinCondition = joinCondition;
        if (contact != null) this.contact = contact;
    }

    /**
     * 일정 변경 (스케줄 텍스트 및 시작/종료일)
     * - 날짜가 변경될 경우 반드시 유효성 검증을 다시 수행함
     */
    public void reschedule(String schedule, LocalDateTime startDate, LocalDateTime endDate) {
        // 날짜가 둘 다 들어왔을 때만 변경 및 검증 수행 (정책에 따라 유연하게 변경 가능)
        if (startDate != null && endDate != null) {
            validateDate(startDate, endDate);
            this.startDate = startDate;
            this.endDate = endDate;
        }

        if (schedule != null) {
            this.schedule = schedule;
        }
    }

    /**
     * 모집 인원 변경
     */
    public void updateRecruitment(Integer recruitingNumber, Integer totalNumber) {
        // 둘 중 하나만 들어오는 경우도 고려해야 하지만, 보통 인원 변경은 같이 일어나는 경우가 많음
        Integer newRecruiting = (recruitingNumber != null) ? recruitingNumber : this.recruitingNumber;
        Integer newTotal = (totalNumber != null) ? totalNumber : this.totalNumber;

        validateRecruitment(newRecruiting, newTotal);

        this.recruitingNumber = newRecruiting;
        this.totalNumber = newTotal;
    }

    /**
     * 상태 변경 (모집중 -> 모집완료 등)
     */
    public void changeStatus(StatusEnum status) {
        if (status == null) return;
        this.status = status;
    }

    /**
     * 작성자 검증 (권한 체크)
     */
    public void validateHost(User user) {
        if (!this.user.getKakaoUserNumber().equals(user.getKakaoUserNumber())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }

    /**
     * 삭제 처리 (Soft Delete)
     */
    public void delete() {
        this.status = StatusEnum.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    private void validateDate(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new IllegalArgumentException("시작일은 종료일보다 빨라야 합니다.");
        }
    }

    private void validateRecruitment(Integer current, Integer total) {
        if (current != null && total != null && current > total) {
            throw new IllegalArgumentException("현재 모집된 인원은 총 인원보다 클 수 없습니다.");
        }
    }
}