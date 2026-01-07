package com.example.weterview.entity;

import com.example.weterview.entity.studyGroup.StudyGroup;
import com.example.weterview.enums.ResultCode;
import com.example.weterview.enums.studyMembership.JoinEnum;
import com.example.weterview.exception.CustomException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "study_group_members")
@Comment("사용자와 스터디 그룹 간의 참가 신청 및 상태(신청/수락/거절)를 관리하는 매핑 테이블")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class StudyGroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_group_member_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "study_group_id")
    private StudyGroup studyGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Comment(value = "참가요청 or 수락 or 거절")
    private JoinEnum join;

    @Column(name = "applied_at", nullable = false, updatable = false)
    @CreatedDate
    @Comment(value = "신청한 날짜, 시간")
    private LocalDateTime appliedAt;

    @Column(name = "accepted_at")
    @Comment(value = "수락한 날짜, 시간")
    private LocalDateTime acceptedAt;

    @Column(name = "refused_at")
    @Comment(value = "거절한 날짜, 시간")
    private LocalDateTime refusedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private StudyGroupMember(
            User user, StudyGroup studyGroup, JoinEnum join) {
        this.user = user;
        this.studyGroup = studyGroup;
        this.join = join;
    }

    public static StudyGroupMember create(User user, StudyGroup studyGroup) {
        if (user == null || studyGroup == null) {
            throw new IllegalArgumentException("User and StudyGroup must not be null");
        }

        return StudyGroupMember.builder()
                .user(user)
                .studyGroup(studyGroup)
                .join(JoinEnum.APPLY)
                .build();
    }

    public void acceptJoin() {
        validateCanTransition();
        this.join = JoinEnum.ACCEPT;
        this.acceptedAt = LocalDateTime.now();
    }

    public void refuseJoin() {
        validateCanTransition();
        this.join = JoinEnum.REFUSE;
        this.refusedAt = LocalDateTime.now();
    }

    /**
     * APPLY 상태가 아니면 예외를 발생
     */
    private void validateCanTransition() {
        if (this.join != JoinEnum.APPLY) {
            ResultCode errorCode =
                    this.join == JoinEnum.ACCEPT
                    ? ResultCode.ALREADY_ACCEPTED_MEMBER
                    : ResultCode.ALREADY_REFUSED_MEMBER;
            throw new CustomException(errorCode);
        }
    }

    @PrePersist
    public void setDefaultJoinStatus() {
        if (this.join == null) {
            this.join = JoinEnum.APPLY;
        }
    }
}
