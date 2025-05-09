package com.example.weterview.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "study_memebership")
public class StudyMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_membership_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @ManyToOne
    @JoinColumn(name = "study_group_id")
    private StudyGroup studyGroupId;
}
