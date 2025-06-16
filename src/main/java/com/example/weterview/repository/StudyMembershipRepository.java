package com.example.weterview.repository;

import com.example.weterview.entity.StudyGroup;
import com.example.weterview.entity.StudyMembership;
import com.example.weterview.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudyMembershipRepository extends JpaRepository<StudyMembership, Long> {
    Optional<StudyMembership> findByStudyGroupIdAndUserId(StudyGroup studyGroupId, User userId);
}
