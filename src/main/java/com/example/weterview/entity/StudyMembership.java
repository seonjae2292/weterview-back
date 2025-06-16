package com.example.weterview.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "study_membership")
public class StudyMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_membership_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "kakao_user_number")
    private User kakaoUserNumber;

    @ManyToOne
    @JoinColumn(name = "study_group_id")
    private StudyGroup studyGroupId;
}
