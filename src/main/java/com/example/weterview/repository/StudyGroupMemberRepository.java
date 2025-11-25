package com.example.weterview.repository;

import com.example.weterview.entity.StudyGroup;
import com.example.weterview.entity.StudyGroupMember;
import com.example.weterview.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyGroupMemberRepository extends JpaRepository<StudyGroupMember, Long> {
    Optional<StudyGroupMember> findStudyGroupMemberByUser(User user);

    Optional<List<StudyGroupMember>> findStudyGroupMembersListByStudyGroup(StudyGroup studyGroup);

    Optional<StudyGroupMember> findStudyGroupMemberByUserAndStudyGroup(User user, StudyGroup studyGroup);

    Page<StudyGroupMember> findStudyGroupMembersByUser(User user, Pageable pageable);
}
