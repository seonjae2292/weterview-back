package com.example.weterview.entity;

import com.example.weterview.enums.studyMembership.JoinEnum;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "study_group_members")
@Comment("사용자와 스터디 그룹 간의 참가 신청 및 상태(신청/수락/거절)를 관리하는 매핑 테이블")
@Data
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

    @PrePersist
    public void setDefaultJoinStatus() {
        if (this.join == null) {
            this.join = JoinEnum.APPLY;
        }
    }

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
}
