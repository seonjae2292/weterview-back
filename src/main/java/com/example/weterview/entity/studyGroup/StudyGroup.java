package com.example.weterview.entity.studyGroup;

import com.example.weterview.entity.BaseTimeEntity;
import com.example.weterview.entity.User;
import com.example.weterview.entity.studyGroup.vo.StudyCapacity;
import com.example.weterview.entity.studyGroup.vo.StudyContent;
import com.example.weterview.entity.studyGroup.vo.StudyPeriod;
import com.example.weterview.enums.studyGroup.LocationEnum;
import com.example.weterview.enums.studyGroup.StatusEnum;
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

    @Builder(access = AccessLevel.PRIVATE)
    public StudyGroup(User user, StudyContent content, StudyPeriod period, StudyCapacity capacity, LocationEnum location) {
        this.user = user;
        this.content = content;
        this.period = period;
        this.capacity = capacity;
        this.location = location;
        this.status = StatusEnum.RECRUITING;
    }

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
}