package com.example.weterview.entity;

import com.example.weterview.enums.studyGroup.FieldEnum;
import com.example.weterview.enums.studyGroup.LocationEnum;
import com.example.weterview.enums.studyGroup.StatusEnum;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "study_groups")
@Data
@EntityListeners(AuditingEntityListener.class)
public class StudyGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_group_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "kakao_user_number")
    private User kakaoUserNumber;

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
}
