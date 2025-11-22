package com.example.weterview.entity;

import com.example.weterview.enums.studyMembership.JoinEnum;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "study_membership")
@Comment(value = "사용자가 생성한 스터디 그룹 게시글 매핑 테이블")
@Data
public class StudyMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_membership_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    // 객체 지향적 의미를 살리기 위해서 userId가 아닌 user라는 명으로 변경한다
    // JPA에서 @ManyToOne, @OneToOne Annotation은 엔티티를 참조한다.
    // 그렇기 때문에 user라고 짓는것이 명확하고 직관적이다.
    private User user;

    @ManyToOne
    @JoinColumn(name = "study_group_id")
    private StudyGroup studyGroup;
}
