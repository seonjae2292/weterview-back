package com.example.weterview.repository;

import com.example.weterview.entity.StudyMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyMembershipRepository extends JpaRepository<StudyMembership, Long> {
    Optional<StudyMembership> findById(Long studyGroupId);
}
