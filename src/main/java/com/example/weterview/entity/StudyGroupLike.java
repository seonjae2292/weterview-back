package com.example.weterview.entity;

import com.example.weterview.entity.studyGroup.StudyGroup;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/// 스터디 그룹 게시글 좋아요 테이블
@Entity
@Table(name = "study_group_likes", indexes = {
        @Index(name = "idx_user_liked_created", columnList = "user_id, is_liked, created_at")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyGroupLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_group_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "study_group_id")
    private StudyGroup studyGroup;

    @Column(name = "is_liked")
    private boolean isLiked = false;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    private StudyGroupLike(User user, StudyGroup studyGroup, boolean isLiked) {
        this.user = user;
        this.studyGroup = studyGroup;
        this.isLiked = isLiked;
    }

    public static StudyGroupLike create(User user, StudyGroup studyGroup) {
        return StudyGroupLike.builder()
                .user(user)
                .studyGroup(studyGroup)
                .isLiked(true)
                .build();
    }

    public void toggle(){
        this.isLiked = !this.isLiked;
    }
}
