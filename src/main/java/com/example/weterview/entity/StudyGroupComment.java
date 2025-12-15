package com.example.weterview.entity;

import com.example.weterview.service.StudyGroupCommentService;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/// 스터디 그룹 게시글 코멘트 테이블
@Entity
@Table(name = "study_group_comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class StudyGroupComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_group_comment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "study_group_id")
    private StudyGroup studyGroup;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "content")
    private String content;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public StudyGroupComment(
            StudyGroup studyGroup, User user, String content,
            LocalDateTime createdAt) {
        this.studyGroup = studyGroup;
        this.user = user;
        this.content = content;
        this.createdAt = createdAt;
    }
}
