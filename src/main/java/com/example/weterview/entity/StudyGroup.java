package com.example.weterview.entity;

import com.example.weterview.dto.studyGroup.request.CreateStudyGroupReq;
import com.example.weterview.enums.ErrorCode;
import com.example.weterview.enums.studyGroup.FieldEnum;
import com.example.weterview.enums.studyGroup.LocationEnum;
import com.example.weterview.enums.studyGroup.StatusEnum;
import com.example.weterview.exception.CustomException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/// 스터디 그룹 게시글 테이블
@Entity
@Table(name = "study_groups")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_group_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "field", nullable = false)
    @Comment( value = "분야")
    private FieldEnum field;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Comment( value = "모집중 or 모집완료 or 삭제됨")
    private StatusEnum status = StatusEnum.RECRUITING;

    @Column(name = "title", nullable = false)
    @Comment( value = "제목")
    private String title;

    @Column(name = "sub_title", nullable = false)
    @Comment( value = "부제목")
    private String subTitle;

    @Column(name = "recruiting_number", nullable = false)
    @Comment( value = "현재 참가한 사용자 수")
    private Integer recruitingNumber;

    @Column(name = "total_number", nullable = false)
    @Comment( value = "총 참가 가능한 사용자 수")
    private Integer totalNumber;

    @Column(name = "start_date", nullable = false)
    @Comment( value = "스터디 그룹 시작 날짜")
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    @Comment( value = "스터디 그룹 끝나는 날짜")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "location", nullable = false)
    @Comment( value = "오프라인 스터디 그룹 위치")
    private LocationEnum location;

    @Column(name = "description", nullable = false)
    @Comment( value = "상세설명")
    private String description;

    @Column(name = "schedule", nullable = false)
    @Comment( value = "스터디 상세 일정 ex) 매주 토요일 오후 2시 ~ 5시")
    private String schedule;

    @Column(name = "join_condition", nullable = false)
    @Comment( value = "스터디에 참가할 수 있는 조건 ex) java에 대한 기본기가 있으면 좋겠습니다.")
    private String joinCondition;

    @Column(name = "contact", nullable = false)
    @Comment( value = "연락할 수 있는 방법 ")
    private String contact;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 내가 생성한 스터디 그룹 게시글인지 확인
    public void validateHost(User user) {
        if (!this.user.getKakaoUserNumber().equals(user.getKakaoUserNumber())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }

    public static StudyGroup create(User user, CreateStudyGroupReq req) {
        return StudyGroup.builder()
                .user(user)
                .field(req.getField())
                .title(req.getTitle())
                .subTitle(req.getSubTitle())
                .recruitingNumber(req.getRecruitingNumber())
                .totalNumber(req.getTotalNumber())
                .startDate(LocalDateTime.parse(req.getStartDate()))
                .endDate(LocalDateTime.parse(req.getEndDate()))
                .location(req.getLocation())
                .description(req.getDescription())
                .schedule(req.getSchedule())
                .joinCondition(req.getJoinCondition())
                .contact(req.getContact())
                .status(StatusEnum.RECRUITING)
                .build();
    }
}
